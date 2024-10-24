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
# add default_tags as variable to allow clean merge/override
variable "tags" {
  type = object( {
    Name                        = string
    qpp_owner                   = string
    qpp_incident-response-email = string
    qpp_application             = string
    qpp_environment             = string
    qpp_layer                   = string
    qpp_sensitivity             = string
    qpp_description             = string
    qpp_iac-repo-url            = string
  } )
}