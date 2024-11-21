terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/qppsf-ct-ecs-restart-tf-state"
    region  = "us-east-1"
    encrypt = "true"
  }

    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=3.70.0"
        }
    }
    required_version = "1.9.7"
}

provider "aws" {
  region  = var.region
}

data "aws_caller_identity" "current" {}


# Event bridge rule to Trigger ECS Restart on Weekends
resource "aws_cloudwatch_event_rule" "ecs-restart-cronjob" {
  name        = "ecsct-restart-${var.project_name}"
  description = "This Event bridge rule runs on a Schedule"
  is_enabled  = false
  schedule_expression = var.event_schedule
}
  
# Event Bridge Target to invoke Lambda Function

resource "aws_cloudwatch_event_target" "ecsevent-invoke-lambda" {
    rule = aws_cloudwatch_event_rule.ecs-restart-cronjob.name
    target_id = "ecs-cron-lambda"
    arn = aws_lambda_function.ecs-automate-restart.arn
}

# IAM Role permissions on Lambda Function
resource "aws_iam_role" "lambda_executionrole" {
 name   = "${var.project_name}-lambda_execrole"
 path   = "/delegatedadmin/developer/"
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
  name         = "qppsf_lambda_cloudwatchlog_policy"
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

resource "aws_iam_policy" "lambda_ecs_policy" {
  name         = "qppsf_lambda_ecs_policy"
  path         = "/delegatedadmin/developer/"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ECSTaskPermissions",
            "Effect": "Allow",
            "Action": [
              "ecs:ListTasks",
              "ecs:StopTask",
              "ecs:UpdateService"
            ],
            "Resource": "*"
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

resource "aws_iam_role_policy_attachment" "ecs-lambda" {
  role        = aws_iam_role.lambda_executionrole.name
  policy_arn  = aws_iam_policy.lambda_ecs_policy.arn
}

# Allow permissions for Event bridge to Invoke Lambda
resource "aws_lambda_permission" "allow_lambda" {
  statement_id  = "AllowExecutionFromEventBridge"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.ecs-automate-restart.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.ecs-restart-cronjob.arn
}

# ECS Pagerduty Alerts - Lambda function 
resource "aws_lambda_function" "ecs-automate-restart" {
  filename      = "ecs-restart-automate.zip"
  function_name = "qppsf-restart-ecs-automate"
  role          = aws_iam_role.lambda_executionrole.arn
  handler       = "ecs-restart-automate.lambda_handler"
  timeout       =  "90"
  runtime       = "python3.9"
  environment {
    variables = {
      ecs_service= var.ecs_svc
      ecs_cluster= var.ecs_cluster
    }
  }

  tags = {
    "Name"                = "${var.project_name}-lambda"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "Performs ECS Restart for Conversiontool on a Schedule"
    "qpp:iac-repo-url"    = var.git-origin
  }

}

