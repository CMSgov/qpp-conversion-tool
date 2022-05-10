resource "aws_kinesis_stream" "kinesis-stream-cw-logs" {
  name             = "conversion-tool-${var.environment}-kinesis-stream"
  shard_count      = 1
  retention_period = 192

  encryption_type = "KMS"
  kms_key_id      = "alias/aws/kinesis"

  tags = {
    "Name"                = "${var.project_name}-kinesis-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "Kinesis Stream for Conversiontool"
    "qpp:iac-repo-url"    = var.git-origin
  }
}
