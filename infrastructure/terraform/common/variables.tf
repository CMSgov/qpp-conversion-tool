variable "region" {
  description = "AWS region to provision"
  type        = string
  default = "us-east-1"
}

variable "environment" {
  type = string
  description = "Environment"
}

variable "git-repo" {
  type = string
}

variable "git-org" {
  type = string
  description = "Git Organization"
}

variable "team" {
  type = string
  description = "QPP Team"
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