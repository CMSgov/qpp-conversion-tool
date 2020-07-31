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

variable "app_security_group" {
  type = string
}

variable "vpn_security_group" {
  type = string
}

variable "lb_security_group" {
  type = string
}

variable "task_role_arn" {
  type = string
}
