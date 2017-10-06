variable "app_ami_id" {
  description = "The default AMI ID."
  type        = "string"
}

variable "app_count" {
  description = "App Instance count."
  type        = "string"
  default     = "3"
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

variable "cloudwatch_notification_arn" {
  description = "The ARN for sending CloudWatch notifications."
  type        = "string"
}

variable "data_subnet_cidr_blocks" {
  description = "List of data subnet CIDR blocks."
  type        = "list"
}

variable "data_subnet_ids" {
  description = "List fo data subnet ids."
  type        = "list"
}

variable "dmz_subnet_cidr_blocks" {
  description = "List of dmz subnet CIDR blocks."
  type        = "list"
}

variable "dmz_subnet_ids" {
  description = "List of dmz subnet ids."
  type        = "list"
}

variable "elb_access_logs_bucket" {
  description = "The S3 bucket for ELB access logs."
  type        = "string"
}

variable "elb_internal" {
  description = "Is the ELB internal-only."
  type        = "string"
}

variable "iam_instance_profile" {
  description = "The IAM instance profile."
  type        = "string"
}

variable "instance_types" {
  description = "EC2 instance types for various services."
  type        = "map"

  default = {
    app  = "m3.medium"
    jump = "m4.large"
  }
}

variable "key_name" {
  description = "The EC2 SSH key name."
  type        = "string"
}

variable "s3_bucket" {
  description = "The S3 bucket for the VPC."
  type        = "string"
}

variable "ssl_certificate_id" {
  description = "The SSL cert ID for the ELB"
  type        = "string"
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
