# QPPSE-1208
locals {
  s3bucket_tags = {
    Name                = "${var.project_name}-s3-logs-bucket"
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_layer           = "Application"
    qpp_sensitivity     = var.sensitivity
    qpp_description     = "Conversiontool Logs S3 Bucket"
    qpp_iac-repo-url    = var.git-origin
  }
}
## This sets up the bucket where all the other bucket's S3 logs are written.
## Most of the config is the same as "secure-bucket" but shorter retention dates
## Note this bucket is
resource "aws_s3_bucket" "log_bucket" {
  bucket = "qppsf-conversion-tool-logs"
  # QPPSE-1211 correct warning:
  # Warning: Argument is deprecated
  # ...
  # Use resource aws_s3_bucket_acl instead
  #
  # acl    = "log-delivery-write"

  # QPPSE-1211 correct warning: 
  # Warning: Argument is deprecated
  # ...
  # Use resource aws_s3_bucket_versioning instead
  #
  # versioning {
  #   enabled = true
  # }
  # QPPSE-1211 correct warning:
  # Warning: Argument is deprecated
  # ... 
  # Use the aws_s3_bucket_lifecycle_configuration resource instead
  # ...
  # lifecycle_rule {
  #   id      = "default-lifecycle"
  #   enabled = true

  #   transition {
  #     days          = 60
  #     storage_class = "STANDARD_IA"
  #   }

  #   transition {
  #     days          = 90
  #     storage_class = "GLACIER"
  #   }

  #   expiration {
  #     days                         = 365
  #     expired_object_delete_marker = true
  #   }
  # }

  lifecycle {
    prevent_destroy = true
    # QPPSE-1211 for resource additions below
    ignore_changes = [ grant ]
  }
  # ...
  # QPPSE-1211 correct warning:
  # Warning: Argument is deprecated
  # ... 
  # Use the aws_s3_bucket_server_side_encryption_configuration resource instead
  # ...
  # Require encryption at rest 
  # server_side_encryption_configuration {
  #   rule {
  #     apply_server_side_encryption_by_default {
  #       sse_algorithm = "AES256"
  #     }
  #   }
  # }
  #

# QPPSE-1208
  tags = merge(var.tags,local.s3bucket_tags)

}

# QPPSE-1211
data "aws_canonical_user_id" "current" {}

resource "aws_s3_bucket_server_side_encryption_configuration" "log_bucket" {
  bucket = aws_s3_bucket.log_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_versioning" "log_bucket" {
  bucket = aws_s3_bucket.log_bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}
resource "aws_s3_bucket_lifecycle_configuration" "log_bucket" {
  bucket = aws_s3_bucket.log_bucket.id

  rule {
    id = "default-lifecycle"

    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }

    expiration {
      days                         = 365
      expired_object_delete_marker = false
    }

    filter {}
    #   and {
    #     prefix = "/"

    #     tags = {
    #       rule      = "log"
    #       autoclean = "true"
    #     }
    #   }
    # }

    status = "Enabled"

    transition {
      days          = 60
      storage_class = "STANDARD_IA"
    }

    transition {
      days          = 90
      storage_class = "GLACIER"
    }
  }

  # rule {
  #   id = "prevent-destroy"
  #   filter {}

  # }
  # rule {
  #   id = "ignore-grant"
  #   filter {}
  #   ignore_changes = [
  #     grant
  #   ]
  #   status = "Enabled"
  # }
}

# QPPSE-1461
# resource "aws_s3_bucket_ownership_controls" "log_bucket" {
#   bucket = aws_s3_bucket.log_bucket.id
#   rule {
#     object_ownership = "BucketOwnerPreferred"
#   }
# }

resource "aws_s3_bucket_acl" "log_bucket" {
  # depends_on = [aws_s3_bucket_ownership_controls.log_bucket]

  bucket = aws_s3_bucket.log_bucket.id

  access_control_policy {
    grant {
      grantee {
        id   = data.aws_canonical_user_id.current.id
        type = "CanonicalUser"
      }
      permission = "FULL_CONTROL"
    }

    grant {
      grantee {
        type = "Group"
        uri  = "http://acs.amazonaws.com/groups/s3/LogDelivery"
      }
      permission = "READ_ACP"
    }

    grant {
      grantee {
        type = "Group"
        uri  = "http://acs.amazonaws.com/groups/s3/LogDelivery"
      }
      permission = "WRITE"
    }

    owner {
      id = data.aws_canonical_user_id.current.id
    }
  }
}
#

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
