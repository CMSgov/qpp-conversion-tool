variable "app_ami_id" {
  description = "The default AMI ID."
  type        = "string"
}

variable "aws_region" {
  description = "EC2 Region for the VPC"
  type        = "string"
}

variable "app_subnet_cidr_blocks" {
  description = "List of app subnet CIDR blocks."
  type        = "list"
}

variable "app_subnet_ids" {
  description = "List of app subnet ids."
  type        = "list"
}

variable "dmz_subnet_cidr_blocks" {
  description = "List of data subnet CIDR blocks."
  type        = "list"
}

variable "dmz_subnet_ids" {
  description = "List of dmz subnet ids."
  type        = "list"
}

variable "app_count" {
  description = "App Instance count."
  type        = "string"
  default     = "3"
}

variable "iam_instance_profile" {
  description = "The IAM instance profile."
  type        = "string"
}

variable "instance_types" {
  description = "EC2 instance types for various services."
  type        = "map"

  default = {
    app   = "m3.medium"
    jump  = "m4.large"
  }
}

variable "ssl_certificate_id" {
  description = "The SSL cert ID for the ELB"
  type = "string"
}

variable "stack_tag" {
  description = "Stack name e.g. dev, test or prod."
  type        = "string"
}

variable "vpc_cidr" {
  description = "The CIDR block for the VPC."
  type        = "string"
}

variable "vpc_id" {
  description = "VPC id."
  type        = "string"
}

variable "vpc_name" {
  description = "The VPC's name."
  type        = "string"
}

variable "wait_for_elb_capacity" {
  description = "wait_for_elb_capacity value."
  type        = "string"

  # over-ride this for prod and load testing envs!
  default = "1"
}
