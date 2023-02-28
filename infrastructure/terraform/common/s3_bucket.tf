data "aws_caller_identity" "current" {}

locals {
    account_id = data.aws_caller_identity.current.account_id
}

resource "aws_s3_bucket" "codepipeline_s3_bucket" {
  bucket = "${var.team}-codepipeline-s3-${local.account_id}-${var.region}"
  acl    = "private"

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }

  versioning {
    enabled = true
  }

  lifecycle {
    prevent_destroy = true
  }

  tags = {
    "qpp:owner"           = var.owner
    "qpp:environment"     = var.environment
    "qpp:Name"            = "qppsf"
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:sensitivity"     = var.sensitivity
    "qpp:application"     = "qppsf"
    "qpp:description"     = "Conversion Tool Artifact S3 Bucket for Codepipeline"
  }
}

resource "aws_s3_bucket_public_access_block" "block_public_access" {
  bucket = aws_s3_bucket.codepipeline_s3_bucket.id

  restrict_public_buckets = true
  ignore_public_acls      = true
  block_public_acls       = true
  block_public_policy     = true
}

output "tf_s3_bucket" {
  value = aws_s3_bucket.codepipeline_s3_bucket.id
}

resource "aws_s3_bucket_policy" "bucket_policy" {
  bucket = aws_s3_bucket.codepipeline_s3_bucket.id
  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowSSLRequestsOnly",
      "Effect": "Deny",
      "Principal": "*",
      "Action": "s3:*",
      "Resource": [
        "${aws_s3_bucket.codepipeline_s3_bucket.arn}",
        "${aws_s3_bucket.codepipeline_s3_bucket.arn}/*"
      ],
      "Condition": {
        "Bool": {
          "aws:SecureTransport": "false"
        }
      }
    }
  ]
}
POLICY
}