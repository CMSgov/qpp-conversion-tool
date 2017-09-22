variable "app_ami_git_hash" {
  type = "string"
}

variable "app_count" {
  default = "3"
}

variable "aws_region" {
  default = "us-east-1"
}

variable "cloudwatch_notification_arn" {
  default = "arn:aws:sns:us-east-1:003384571330:mpl-alarm"
}

variable "elb_access_logs_bucket" {
  default = "aws-hhs-cms-ccsq-qpp-navadevops-us-east-1"
}

variable "iam_instance_profile" {
  default = "server-prod"
}

variable "instance_types" {
  default = {
    app  = "m4.large"
    jump = "m3.medium"
  }
}

variable "key_name" {
  default = "nava-sandbox"
}

variable "s3_bucket" {
  default = "aws-hhs-cms-ccsq-qpp-navadevops-prod-us-east-1"
}

variable "ssl_certificate_id" {
  default = "arn:aws:acm:us-east-1:003384571330:certificate/a54f7f91-5268-4321-b2ba-f282e65a5c4e"
}

variable "stack_tag" {
  default = "prod"
}

variable "vpc_name" {
  default = "qpp-qrda3converter-prod"
}
