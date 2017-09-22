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
  default = "server-nonprod"
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
  default = "aws-hhs-cms-ccsq-qpp-navadevops-nonprod-us-east-1"
}

variable "ssl_certificate_id" {
  default = "arn:aws:acm:us-east-1:003384571330:certificate/0fb69207-0392-478a-8099-66fc99baa0d9"
}

variable "stack_tag" {
  default = "impl"
}

variable "vpc_name" {
  default = "qpp-qrda3converter-impl"
}
