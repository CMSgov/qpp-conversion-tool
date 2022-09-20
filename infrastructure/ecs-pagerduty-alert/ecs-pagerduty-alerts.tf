terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/qppsf-ct-pgduty-alerts-tf-state"
    region  = "us-east-1"
    encrypt = "true"
  }

    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=3.70.0"
        }
    }
    required_version = "1.0.0"
}

provider "aws" {
  region  = var.region
}

data "aws_caller_identity" "current" {}


# Event bridge rule to Monitor ECS Cluster Tasks State
resource "aws_cloudwatch_event_rule" "ecs-task-monitor" {
  name        = "ecsct-task-monitor-${var.project_name}-${var.environment}"
  description = "This Event bridge rule Monitors ECS clusters of prod and impl and triggers upon a Task State Change"

  event_pattern = <<EOF
{
  "source": ["aws.ecs"],
  "detail-type": ["ECS Task State Change"],
  "detail": {
    "clusterArn": ["arn:aws:ecs:us-east-1:003384571330:cluster/qppsf-conversion-tool-prod", "arn:aws:ecs:us-east-1:003384571330:cluster/qppsf-conversion-tool-impl"]
  }
}
EOF
}

# Event Bridge Target to invoke Lambda Function

resource "aws_cloudwatch_event_target" "ecsevent-invoke-lambda" {
    rule = aws_cloudwatch_event_rule.ecs-task-monitor.name
    target_id = "ecs-task-event-lambda"
    arn = aws_lambda_function.ecs-pagerduty-alerts.arn
}

# IAM Role permissions on Lambda Function

resource "aws_iam_role" "lambda_executionrole" {
 name   = "ecsct_lambda_executionrole--${var.project_name}-${var.environment}"
 path = "/delegatedadmin/developer/"
 permissions_boundary = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/cms-cloud-admin/developer-boundary-policy"
 assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": "AllowLambda"
    }
  ]
}
EOF
}

# IAM policy for Lambda to submit to Cloudwatch Logs
resource "aws_iam_policy" "lambda_cloudwatch_logging" {

  name         = "ecs_lambda_cloudwatch_policy"
  path         = "/delegatedadmin/developer/"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*",
      "Effect": "Allow"
    }
  ]
}
EOF
}

# IAM policy for Lambda permissions to publish to SES

resource "aws_iam_policy" "lambda_publish_to_ses" {

  name         = "ecs_lambda_ses_policy"
  path         = "/delegatedadmin/developer/"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ses:SendEmail",
                "ses:SendRawEmail"
            ],
            "Resource": "*"
        }
    ]
}
EOF  
}

#IAM policy attachment for SES

resource "aws_iam_role_policy_attachment" "ses-lambda" {
  role        = aws_iam_role.lambda_executionrole.name
  policy_arn  = aws_iam_policy.lambda_publish_to_ses.arn
  
}

#IAM policy attachment for Cloudwatch Logging
resource "aws_iam_role_policy_attachment" "cloudwatchlogs-lambda" {
  role        = aws_iam_role.lambda_executionrole.name
  policy_arn  = aws_iam_policy.lambda_cloudwatch_logging.arn
}

# Allow permissions for Event bridge to Invoke Lambda

resource "aws_lambda_permission" "allow_cloudwatch" {
  statement_id  = "AllowExecutionFromCloudWatch"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.ecs-pagerduty-alerts.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.ecs-task-monitor.arn
}

# ECS Pagerduty Alerts - Lambda function 
resource "aws_lambda_function" "ecs-pagerduty-alerts" {
  filename      = "ecs-pgduty-alerts.zip"
  function_name = "ecs-pgduty-alerts"
  role          = aws_iam_role.lambda_executionrole.arn
  handler       = "ecs-pgduty-alerts.lambda_handler"
  timeout       =  "120"
  runtime = "python3.9"

  tags = {
    "Name"                = "${var.project_name}-lambda-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "ECS State monitoring Lambda for Conversiontool"
    "qpp:iac-repo-url"    = var.git-origin
  }

}

