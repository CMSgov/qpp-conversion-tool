variable "project_name" {
  description = "Name of the Project"
  type        = string
  default     = "qppsf-ct"
}

variable "region" {
  type    = string
  default = "us-east-1"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "slack_hook_url" {
  type = string
  description = "Slack webhook p-qpp-sub channel"
}

variable "owner" {
  type = string
}

variable "application" {
  type = string
}