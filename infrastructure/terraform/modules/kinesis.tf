# QPPSE-1208
locals {
  kinesis_tags = {
    Name                = "${var.project_name}-kinesis-${var.environment}"
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = "Application"
    qpp_sensitivity     = var.sensitivity
    qpp_description     = "Kinesis Stream for Conversiontool"
    qpp_iac-repo-url    = var.git-origin
  }
}

resource "aws_kinesis_stream" "kinesis-stream-cw-logs" {
  name             = "conversion-tool-${var.environment}-kinesis-stream"
  shard_count      = 1
  retention_period = 192

  encryption_type = "KMS"
  kms_key_id      = "alias/aws/kinesis"

  tags = merge(var.tags,local.kinesis_tags)
}
