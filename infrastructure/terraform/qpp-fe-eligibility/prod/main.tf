terraform {
  backend "s3" {
    bucket  = "qppfe-terraform-state"
    key     = "workbench/efs-prod.tfstate"
    region  = "us-east-1"
    encrypt = "true"
  }

    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=3.70.0"
        }
    }
    required_version = "1.0.0"
}

provider "aws" {
  region  = var.region
}

module "qpp-fe-eligibility" {
  source = "../modules"

  // EFS 
  efs_creation_token  = var.efs_creation_token
  efs_kms_key_id      = var.efs_kms_key_id
  efs_environment     = var.efs_environment
  efs_Name            = var.efs_Name
  efs_application     = var.efs_application
  efs_description     = var.efs_description
  efs_iac-repo-url    = var.efs_iac-repo-url
  efs_owner           = var.efs_owner
  efs_pagerduty-email = var.efs_pagerduty-email
  efs_sensitivity     = var.efs_sensitivity
}
