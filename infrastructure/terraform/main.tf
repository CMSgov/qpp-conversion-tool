# Bucket must exist before running
# Ensure it's private and has versioning enabled

terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/conversion-tool.tfstate"
    region  = "us-east-1"
    encrypt = "true"
  }
}

provider "aws" {
  region  = var.region
  version = "~> 2.70"
}

