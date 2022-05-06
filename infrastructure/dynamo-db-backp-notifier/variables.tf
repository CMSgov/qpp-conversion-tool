variable "project_name" {
  description = "Name of the Project"
  type        = string
  default     = "qppsf"
}

variable "region" {
  type    = string
  default = "us-east-1"
}

variable "environment" {
  type    = string
  default = "dev"
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
