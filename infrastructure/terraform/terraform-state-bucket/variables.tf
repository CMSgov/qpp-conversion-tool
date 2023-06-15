variable "region" {
  description = "The AWS region to use"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Team or Project"
  type        = string
  default     = "qppsf-ct"
}

variable "environment" {
  description = "Name of the Environment"
  type        = string
  default     = "common"
}

variable "owner" {
  description = "Resource Owner"
  type        = string
  default     = "qpp-final-scoring-devops@semanticbits.com"
}

variable "pagerduty_email" {
  description = "Team pagerduty notifications email endpoint"
  type        = string
  default     = "qpp-final-scoring-devops@semanticbits.com"
}

variable "application" {
  type        = string
  default     = "qpp-conversion-tools"
}

variable "sensitivity" {
  type        = string
  default     = "confidential"
}

variable "git-origin" {
  type        = string
  default     = "https://https://github.com/CMSgov/qpp-conversion-tool.git"
}