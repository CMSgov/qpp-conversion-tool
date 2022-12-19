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

variable "git-provider-url" {
    type = string
    description = "Provider URL"

}

variable "git-org" {
  type = string
  description = "Git Organization"
}

variable "team" {
    type = string
    description = "QPP Team"
    default = "qppsf"

}