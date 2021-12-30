## This sets up the bucket where all the other bucket's S3 logs are written.
## Most of the config is the same as "secure-bucket" but shorter retention dates
## Note this bucket is
resource "aws_s3_bucket" "log_bucket" {
  bucket = "qppsf-conversion-tool-logs"
  acl    = "log-delivery-write"

  versioning {
    enabled = true
  }

  lifecycle_rule {
    id      = "default-lifecycle"
    enabled = true

    transition {
      days          = 90
      storage_class = "STANDARD_IA"
    }

    transition {
      days          = 120
      storage_class = "GLACIER"
    }
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
    Name            = "${var.project_name}-s3",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }

}


# No public access
resource "aws_s3_bucket_public_access_block" "log_bucket-public-access-block" {
  bucket = aws_s3_bucket.log_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Attach the common policy denying unecrypted communication  to each bucket
resource "aws_s3_bucket_policy" "log_bucket" {
  bucket = aws_s3_bucket.log_bucket.id

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DenyUnencryptedCommunication",
      "Action": "s3:*",
      "Effect": "Deny",
      "Resource": [
         "arn:aws:s3:::${aws_s3_bucket.log_bucket.id}",
         "arn:aws:s3:::${aws_s3_bucket.log_bucket.id}/*"
        ],
      "Condition": {
        "Bool": {
          "aws:SecureTransport": "false"
        }
      },
      "Principal": "*"
    },
    {
        "Sid": "ELBAccessLogging",
        "Effect": "Allow",
        "Principal": {
            "AWS": "arn:aws:iam::127311923021:root"
        },
        "Action": "s3:PutObject",
        "Resource": "arn:aws:s3:::${aws_s3_bucket.log_bucket.id}/*"
    },
    {
        "Sid": "AWSLogDeliveryWriteforNLB",
        "Effect": "Allow",
        "Principal": {
            "Service": [
                "delivery.logs.amazonaws.com"
            ]
        },
        "Action": "s3:GetBucketAcl",
        "Resource": "arn:aws:s3:::${aws_s3_bucket.log_bucket.id}"
    },
    {
        "Sid": "AWSLogDeliveryAclCheckforNLB",
        "Effect": "Allow",
        "Principal": {
            "Service": [
                "delivery.logs.amazonaws.com"
            ]
        },
        "Action": "s3:PutObject",
        "Resource": "arn:aws:s3:::${aws_s3_bucket.log_bucket.id}/*",
        "Condition": {
            "StringEquals": {
                "s3:x-amz-acl": "bucket-owner-full-control"
            }
        }
    }
  ]
}
POLICY

}
