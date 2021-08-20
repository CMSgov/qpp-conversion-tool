terraform {
    required_version = "0.13.7"
    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=3.52.0"
        }
    }
}

provider "aws" {
    region = "us-east-1"
}

terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/qppsf-ddb-backup-notifier-tf-state"
    region  = "us-east-1"
    encrypt = "true"
  }
}

data "aws_caller_identity" "current" {}

# Event bridge rule to Invoke Lambda Function on a Schedule
resource "aws_cloudwatch_event_rule" "invoke-lambda-on-schedule" {
  name        = "invoke-lambda-scheduler-${var.project_name}-${var.environment}"
  description = "This Event bridge Invokes Lambda function on a Schedule"
  schedule_expression = "cron(0 16 ? * * *)"

}

# Event Bridge Target to invoke Lambda Function

resource "aws_cloudwatch_event_target" "ddbackup-invoke-lambda" {
    rule = aws_cloudwatch_event_rule.invoke-lambda-on-schedule.name
    target_id = "invoke-ddb-lambda"
    arn = aws_lambda_function.ddb-notification-alerts.arn
}

# IAM Role permissions on Lambda Function

resource "aws_iam_role" "lambda_executionrole" {
 name   = "dynamodbbackup_lambda_executionrole-${var.project_name}-${var.environment}"
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
  name         = "ddbackup_lambda_cloudwatch_policy"
  description = "IAM permissions for Lambda to Create Log Group"
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

# IAM policy for permissions on Dynamo DB Backups
resource "aws_iam_policy" "lambda_ddb_backups" {
  name         = "dynamodb_backup_lambda_policy"
  description = "IAM permission to list dynamo Db Backups"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "dynamodb:DescribeTable",
                "dynamodb:Scan",
                "dynamodb:DescribeBackup",
                "dynamodb:ListGlobalTables",
                "dynamodb:ListTables",
                "dynamodb:ListBackups",
                "dynamodb:ListStreams"
            ],
            
            "Resource": [
                "arn:aws:dynamodb:us-east-1:003384571330:table/qpp-qrda3converter-prod-metadata/backup/*",
                "arn:aws:dynamodb:us-east-1:003384571330:table/qpp-qrda3converter-prod-metadata"
            ]
        }
    ]
}
EOF
}

#IAM policy attachment for permissions on Dynamo DB
resource "aws_iam_role_policy_attachment" "dynamodb-lambda" {
  role        = aws_iam_role.lambda_executionrole.name
  policy_arn  = aws_iam_policy.lambda_ddb_backups.arn
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
  function_name = aws_lambda_function.ddb-notification-alerts.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.invoke-lambda-on-schedule.arn
}

# Dynamo DB Backup Notification - Lambda function 
resource "aws_lambda_function" "ddb-notification-alerts" {
  filename      = "dynamo-db-bckup-notifier.zip"
  function_name = "dynamo-db-bckup-notifier"
  role          = aws_iam_role.lambda_executionrole.arn
  handler       = "dynamo-db-bckup-notifier.lambda_handler"
  timeout       = "120"
  runtime       = "python3.7"
  environment {
    variables = {
      "channel_id" = "GA20H9QJW"
      "slack_channel_webhook" = "https://hooks.slack.com/services/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"
    }
  }


}
