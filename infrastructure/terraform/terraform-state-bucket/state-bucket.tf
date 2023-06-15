resource "aws_s3_bucket" "terraform-state-bucket" {
  bucket = "qppsf-conversion-tool-tf-state"

  tags = {
    "Name"                = "qppsf-conversion-tool-tf-state"
    "qpp:owner"           = var.owner
    "qpp:environment"     = var.environment
    "qpp:Name"            = "qppsf-ct"
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:sensitivity"     = var.sensitivity
    "qpp:application"     = "qppsf-ct"
    "qpp:description"     = "Conversion-Tools Terraform State Bucket"
    "qpp-cross-acc-s3-replication" = "us-east-1"
  }
}

resource "aws_s3_bucket_public_access_block" "terraform-state-bucket_public_block" {
  bucket = aws_s3_bucket.terraform-state-bucket.id

  restrict_public_buckets = true
  ignore_public_acls      = true
  block_public_acls       = true
  block_public_policy     = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "terraform-state-bucket_encryption" {
  bucket = aws_s3_bucket.terraform-state-bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "AES256"
    }
    bucket_key_enabled = false
  }
}

resource "aws_s3_bucket_versioning" "terraform-state-bucket_versioning" {
  bucket = aws_s3_bucket.terraform-state-bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_policy" "terraform-state-bucket_bucket_policy" {
  bucket = aws_s3_bucket.terraform-state-bucket.id
  policy = data.aws_iam_policy_document.terraform-state-bucket_bucket_policy.json
}

data "aws_iam_policy_document" "terraform-state-bucket_bucket_policy" {
  statement {
    sid    = "AllowSSLRequestsOnly"
    effect = "Deny"
    principals {
      type = "*"
      identifiers = ["*"]
    }

    actions = [
      "s3:*",
    ]
    
    resources = [
      aws_s3_bucket.terraform-state-bucket.arn,
      "${aws_s3_bucket.terraform-state-bucket.arn}/*",
    ]

    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values = ["false"]
    }
  }
}