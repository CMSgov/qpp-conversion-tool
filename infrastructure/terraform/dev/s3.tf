# S3 Bucket to securely store certificates for Conversion-Tool

resource "aws_s3_bucket" "certs_bucket" {
  bucket = "qppsf-conversion-tool-artifacts-ssl-bucket"
  acl    = "private"

  versioning {
    enabled = true
  }

  lifecycle_rule {
    id      = "default-lifecycle"
    enabled = true
  }

  lifecycle {
    prevent_destroy = true
  }

  # Require encryption at rest
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }
  tags = {
    "Name"                = "${var.project_name}-s3-certs-bucket-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "S3 Bucket to securely store certificates for Conversion-Tool"
    "qpp:iac-repo-url"    = var.git-origin
  }

}

# No public access
resource "aws_s3_bucket_public_access_block" "certs_bucket-public-access-block" {
  bucket = aws_s3_bucket.certs_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Attach the common policy denying unecrypted communication to each bucket
resource "aws_s3_bucket_policy" "certs_bucket" {
  bucket = aws_s3_bucket.certs_bucket.id

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DenyUnencryptedCommunication",
      "Action": "s3:*",
      "Effect": "Deny",
      "Resource": [
         "arn:aws:s3:::${aws_s3_bucket.certs_bucket.id}",
         "arn:aws:s3:::${aws_s3_bucket.certs_bucket.id}/*"
        ],
      "Condition": {
        "Bool": {
          "aws:SecureTransport": "false"
        }
      },
      "Principal": "*"
    }

  ]
}
POLICY

}