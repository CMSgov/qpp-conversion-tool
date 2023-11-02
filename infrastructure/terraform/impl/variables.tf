variable "project_name" {
  description = "Team or Project"
  type        = string
}

variable "environment" {
  type = string
}

variable "team" {
  type = string
  description = "QPP Team"
}

variable "region" {
  type    = string
  default = "us-east-1"
}

variable "vpc_id" {
  type = string
}

variable "app_subnet1" {
  type = string
}

variable "app_subnet2" {
  type = string
}

variable "app_subnet3" {
  type = string
}

variable "vpn_security_group" {
  type = string
}

variable "lb_security_group" {
  type = string
}

variable "vpc_cidr" {
  type = list
}

variable "pagerduty_email" {
  type = string
}

variable "owner" {
  type = string
}

variable "application" {
  type = string
}

variable "sensitivity" {
  type = string
}

variable "git-origin" {
  type = string
}

variable "certificate_arn" {
  description = "SSL Certificate arn for the environment"
  type        = string
}

variable "codebuild_branch_ref" {
  type = string
  description = "ConversionTool Branch Ref"
}

variable "allow_kms_keys" {
  description = "kms arns to be allowed"
}

# QPPSE-1208
variable "default_tags" {
  description = "default project tags for compliance"
  type = object ({
    Name                        = string
    qpp_owner                   = string  # email_addr
    qpp_incident-response-email = string  # email_addr
    qpp_application             = string
    qpp_environment             = string
    qpp_layer                   = string    # primarily EC2
    qpp_distribution            = optional(string)    # optional, primarily EC2
    cpm-backup                  = optional(string)    # optional, primarily EC2 + RDS
    qpp_expiry-date             = optional(number)    # optional, Unix timestamp
    qpp_source-ami              = optional(string)    # optional, primarily EC2
    qpp_sensitivity             = string
    qpp_description             = string
    qpp_iac-repo-url            = string
  })
  default = {
    Name                        = "qppsf-ct project"
    qpp_owner                   = "qpp-final-scoring-devops@semanticbits.com"  # email_addr
    qpp_incident-response-email = "893a0342-571a-43d4-ad5e-f4b0aef7654b+CT-routingkey-nonprod@alert.victorops.com"  # email_addr
    qpp_application             = "qpp-conversiontool"
    qpp_environment             = "impl"
    qpp_layer                   = "Application"     # primarily EC2
    ###qpp_distribution            = ""            # optional, primarily EC2
    ###cpm-backup                  = ""            # optional, primarily EC2 + RDS
    ###qpp_expiry-date             = 2147483647    # optional, Unix timestamp
    ###qpp_source-ami              = ""            # optional, primarily EC2
    qpp_sensitivity             = "Confidential"
    qpp_description             = "default tag set"
    qpp_iac-repo-url            = "https://github.com/CMSgov/qpp-conversion-tool.git"
  }
}