data "aws_ssm_parameter" "splunk_endpoint" {
  name = "/global/splunk_url"
}

data "aws_ssm_parameter" "splunk_token" {
  name = "/global/splunk_token"
}

resource "aws_lambda_function" "kinesis_cw_lambda" {
  filename = "${path.module}/Kinesis-cloudwatch-splunk.zip"
  function_name = "ConversionTool-${var.environment}"
  role = "${aws_iam_role.kinesis_lambda_role.arn}"
  handler = "index.handler"
  runtime = "nodejs14.x"
  timeout = 30

  tags = {
    "Name"                = "${var.project_name}-lambda-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "Kinesis Lambda for CW logs for Conversiontool"
    "qpp:iac-repo-url"    = var.git-origin
  }

  environment {
    variables = {
      SPLUNK_URL = data.aws_ssm_parameter.splunk_endpoint.value
      SPLUNK_TOKEN = data.aws_ssm_parameter.splunk_token.value
    }
  }
}

resource "aws_lambda_event_source_mapping" "kinesis_lambda_source" {
  event_source_arn = "${aws_kinesis_stream.kinesis-stream-cw-logs.arn}"
  function_name = "${aws_lambda_function.kinesis_cw_lambda.arn}"
  starting_position = "TRIM_HORIZON"
}