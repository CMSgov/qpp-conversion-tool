# Bucket must exist before running
# Ensure it's private and has versioning enabled

terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/conversion-tool-impl.tfstate"
    region  = "us-east-1"
    encrypt = "true"
  }

    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=3.65.0"
        }
    }
    required_version = "0.14.11"
}

provider "aws" {
  region  = var.region
}

module "conversion-tool" {
  source = "../modules"

  pagerduty_email    = var.pagerduty_email
  lb_security_group  = var.lb_security_group
  vpc_cidr           = var.vpc_cidr
  vpn_security_group = var.vpn_security_group
  project_name       = var.project_name
  vpc_id             = var.vpc_id
  app_subnet2        = var.app_subnet2
  app_subnet3        = var.app_subnet3
  owner              = var.owner
  sensitivity        = var.sensitivity
  app_subnet1        = var.app_subnet1
  environment        = var.environment
  application        = var.application
  git-origin         = var.git-origin
  certificate_arn    = var.certificate_arn
  ssm_secret         = var.ssm_secret
}
