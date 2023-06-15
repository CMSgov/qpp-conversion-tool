terraform {
    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=4.67.0"
        }
    }
    required_version = "1.0.0"
}

provider "aws" {
    region = "us-east-1"
}

terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/conversion-tool-tf-state-bucket.tfstate"
    region  = "us-east-1"
    encrypt = "true"
  }
}