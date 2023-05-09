variable "region" {
  type    = string
  default = "us-east-1"
}

// EFS Variables
variable "efs_creation_token" {
  type        = string
}

variable "efs_kms_key_id" {
  type        = string
}

variable "efs_environment" {
  type        = string
}

variable "efs_Name" {
  type        = string
}

variable "efs_application" {
  type        = string
}

variable "efs_description" {
  type        = string
}

variable "efs_iac-repo-url" {
  type        = string
}

variable "efs_owner" {
  type        = string
}

variable "efs_pagerduty-email" {
  type        = string
}

variable "efs_sensitivity" {
  type        = string
}
