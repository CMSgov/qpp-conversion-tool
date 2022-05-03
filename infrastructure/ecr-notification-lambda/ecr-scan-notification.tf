terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/conversion-tool-ecr-notification.tfstate"
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

data "aws_caller_identity" "current" {
}

resource "aws_ssm_parameter" "slack_hook_url" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/slack_hook_url"
  description = "Slack Webhook Url for Scoring"
  type        = "SecureString"
  value       = var.slack_hook_url
  overwrite   = true
  
  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    "Name"                = "${var.project_name}-ssm-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = "Confidential"
    "qpp:description"     = "SSM Param for Conversiontool"
    "qpp:iac-repo-url"    = var.git-origin
  }
}

# Event Rule to monitor ECR imaage scan which is set to complete
resource "aws_cloudwatch_event_rule" "ecr-scan-notification" {
  name        = "ecr-scan-notification-${var.project_name}-${var.environment}"
  description = "Triggers an event upon completition of ECR image scan"

  event_pattern = <<EOF
{
  "source": ["aws.ecr"],
  "detail-type": ["ECR Image Scan"],
  "detail":{
      "image-tags": ["latest"],
      "scan-status": ["COMPLETE"],
      "repository-name": ["qppsf/conversion-tool/dev","qppsf/conversion-tool/impl","qppsf/conversion-tool/devpre","qppsf/conversion-tool/prod"]
  }
}
EOF
}

# Event Bridge Target to invoke Lambda Function
resource "aws_cloudwatch_event_target" "ecrevent-invoke-lambda" {
    rule = aws_cloudwatch_event_rule.ecr-scan-notification.name
    target_id = "ecr-scan-event-lambda"
    arn = aws_lambda_function.ecr-lambda-notification.arn
}

# IAM Role permissions on Lambda Function
resource "aws_iam_role" "lambda_executionrole" {
 name   = "ecr-lambda-execrole-${var.project_name}-${var.environment}"
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

  name         = "ecr_lambda_cloudwatch_policy"
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

#IAM policy attachment for Cloudwatch Logging
resource "aws_iam_role_policy_attachment" "cloudwatchlogs-lambda" {
  role        = aws_iam_role.lambda_executionrole.name
  policy_arn  = aws_iam_policy.lambda_cloudwatch_logging.arn
}

# Allow permissions for Event bridge to Invoke Lambda

resource "aws_lambda_permission" "allow_cloudwatch" {
  statement_id  = "AllowExecutionFromCloudWatch"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.ecr-lambda-notification.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.ecr-scan-notification.arn
}

# ECR notification Lambda
resource "aws_lambda_function" "ecr-lambda-notification" {
  filename      = "ecr-lambda-notification.zip"
  function_name = "ecr-lambda-notification"
  role          = aws_iam_role.lambda_executionrole.arn
  handler       = "ecr-lambda-notification.lambda_handler"
  timeout       =  "120"
  runtime = "python3.7"

  tags = {
    "Name"                = "${var.project_name}-lambda-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "ECR Notification Lambda Conversiontool"
    "qpp:iac-repo-url"    = var.git-origin
  }
  environment {
    variables = {
        "channel" = "p-qpp-sub-alerts"
        "hook_url" = var.slack_hook_url

    }
  }

}