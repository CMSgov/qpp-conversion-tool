variable "project_name" {
  description = "Team or Project"
  type        = string
}

variable "environment" {
  type = string
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

variable "team" {
  type = string
  description = "QPP Team"
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

# QPPSE-1208
variable "default_tags" {
  description = "default project tags for compliance"
  type = object ({
    Name                        = string
    qpp_owner                   = string  # email_addr
    qpp_incident-response-email = string  # email_addr
    qpp_application             = string
    # one of:
    # qpp-ar
    # qpp-auth
    # qpp-claims
    # qpp-epcs
    # qpp-frontend
    # qpp-conversiontool
    # qpp-eligibility
    # qpp-scoring
    # qpp-selfnomination
    # qpp-sfui
    # qpp-clinicians-api
    # qpp-scoring-api
    # qpp-submissions-api
    # qpp-targetedreview
    # qpp-webinterface
    # qpp-qa
    # qpp-secops
    # cqr-ui
    qpp_environment             = string
    # one of:
    # dev
    # test
    # impl 
    # devpre 
    # prod
    qpp_layer                   = string    # primarily EC2
    qpp_distribution            = string    # optional, primarily EC2
    cpm-backup                  = string    # optional, primarily EC2 + RDS
    qpp_expiry-date             = number    # optional, Unix timestamp
    qpp_source-ami              = string    # optional, primarily EC2
    qpp_sensitivity             = string
    # one of phi, pii, fti, confidential, public [default]
    qpp_description             = string
    qpp_iac-repo-url            = string
    # starts with 
    # https://github.cms.gov/[repo-path]
    # https://github.com/cmsgov
    # or 'na'
  })
  default = {
    Name                        = "qppsf-ct project"
    qpp_owner                   = "qpp-final-scoring-devops@semanticbits.com"  # email_addr
    qpp_incident-response-email = "qpp-final-scoring-devops@semanticbits.com"  # email_addr
    qpp_application             = "qpp-conversiontool"
    qpp_environment             = "dev"
    qpp_layer                   = "utility"     # primarily EC2
    qpp_distribution            = ""            # optional, primarily EC2
    cpm-backup                  = ""            # optional, primarily EC2 + RDS
    qpp_expiry-date             = 2147483647    # optional, Unix timestamp
    qpp_source-ami              = ""            # optional, primarily EC2
    qpp_sensitivity             = "PII/PHI"
    qpp_description             = "default tag set"
    qpp_iac-repo-url            = "https://github.com/CMSgov/qpp-conversion-tool"
  }
}