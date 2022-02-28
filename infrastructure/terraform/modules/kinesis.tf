resource "aws_kinesis_stream" "kinesis-stream-cw-logs" {
  name             = "conversion-tool-${var.environment}-kinesis-stream"
  shard_count      = 1
  retention_period = 192

  encryption_type = "KMS"
  kms_key_id      = "alias/aws/kinesis"

  tags = {
    owner           = var.owner
    environment     = var.environment
    project         = var.project_name
    application     = var.application
    sensitivity     = var.sensitivity
  }
}
