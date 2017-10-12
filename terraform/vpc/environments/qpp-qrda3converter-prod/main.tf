provider "aws" {
  region = "${var.aws_region}"
}

terraform {
  backend "s3" {
    bucket         = "aws-hhs-cms-ccsq-qpp-navadevops-prod-us-east-1"
    key            = "qpp-qrda3converter-prod/terraform/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "tf_lock"
  }

  required_version = "~> 0.10.4"
}

data "aws_ami" "app" {
  most_recent = true

  filter {
    name = "tag-key"
    values = ["application_git_hash"]
  }

  filter {
    name = "tag-value"
    values = ["${var.app_ami_git_hash}"]
  }
}

module "network" {
  source = "git@github.com:CMSgov/corevpc//terraform/modules/aws/network-v3?ref=6ad748bcec4adc91a3d7197db0af68d0726f6207"

  vpc_name = "${var.vpc_name}"
}

module "qpp-qrda3converter" {
  source = "../../templates/qpp-qrda3converter"

  app_ami_id                  = "${data.aws_ami.app.image_id}"
  app_count                   = "${var.app_count}"
  app_subnet_cidr_blocks      = "${module.network.app_subnet_cidr_blocks}"
  app_subnet_ids              = "${module.network.app_subnet_ids}"
  aws_region                  = "${var.aws_region}"
  cloudwatch_notification_arn = "${var.cloudwatch_notification_arn}"
  data_subnet_cidr_blocks     = "${module.network.data_subnet_cidr_blocks}"
  data_subnet_ids             = "${module.network.data_subnet_ids}"
  dmz_subnet_cidr_blocks      = "${module.network.dmz_subnet_cidr_blocks}"
  dmz_subnet_ids              = "${module.network.dmz_subnet_ids}"
  elb_access_logs_bucket      = "${var.elb_access_logs_bucket}"
  elb_internal                = "true"
  iam_instance_profile        = "${var.iam_instance_profile}"
  instance_types              = "${var.instance_types}"
  key_name                    = "${var.key_name}"
  s3_bucket                   = "${var.s3_bucket}"
  stack_tag                   = "${var.stack_tag}"
  vpc_cidr                    = "${module.network.vpc_cidr}"
  vpc_id                      = "${module.network.vpc_id}"
  vpc_name                    = "${var.vpc_name}"
  wait_for_elb_capacity       = "${var.app_count}"
}
