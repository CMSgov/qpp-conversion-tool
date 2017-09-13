data "template_cloudinit_config" "app_config" {
  gzip          = true
  base64_encode = true

  part {
    content_type = "text/x-shellscript"
    content      = "${module.system_user_data.header}"
  }

  part {
    content_type = "text/x-shellscript"
    content      = "${module.app_service_user_data.user_data}"
  }

  part {
    content_type = "text/x-shellscript"
    content      = "${module.system_user_data.footer}"
  }

  part {
    content_type = "text/x-shellscript"
    content      = "${module.app_service_user_data.user_data_commands}"
  }
}

module "app_service_user_data" {
  source = "git@github.com:CMSgov/corevpc//terraform/modules/corevpc/service_user_data?ref=2ce4c5a916a01c9bc9905b123728986da7dbff39"

  name = "app"

  startup_commands = <<EOF
chkconfig --add coreserver
chkconfig coreserver on

service coreserver restart
EOF
}

module "app" {
  source = "git@github.com:CMSgov/corevpc//terraform/modules/aws/asg/web-service?ref=1e3fc919b1f258ef15ddf7ea36e5f3ece1013e8a"

  ami_id                      = "${var.app_ami_id}"
  aws_region                  = "${var.aws_region}"
  cloudwatch_notification_arn = "${var.cloudwatch_notification_arn}"
  count                       = "${var.app_count}"
  elb_access_logs_bucket      = "${var.elb_access_logs_bucket}"
  elb_cidr_blocks             = ["${var.data_subnet_cidr_blocks}"]
  elb_instance_port           = "3000"
  elb_instance_protocol       = "http"
  elb_internal                = "true"
  elb_port                    = "443"
  elb_protocol                = "https"
  elb_security_groups         = "${concat(list(module.open_http.id)}"
  elb_subnet_ids              = ["${var.data_subnet_ids}"]
  iam_instance_profile        = "${var.iam_instance_profile}"
  instance_type               = "${lookup(var.instance_types, "app")}"
  jump_security_group_id      = "${module.jump.security_group_id}"
  key_name                    = "${var.key_name}"
  layer                       = "app"
  route53_zone_id             = "${module.route53_hosted_zone.zone_id}"
  subnet_ids                  = "${var.app_subnet_ids}"

  tags = [
    {
      key                 = "Name"
      value               = "${var.vpc_name}-app"
      propagate_at_launch = true
    },
    {
      key                 = "gov:cms:inventory:Environment"
      value               = "Test"
      propagate_at_launch = true
    },
    {
      key                 = "gov:cms:inventory:Creator"
      value               = "Nava PBC"
      propagate_at_launch = true
    },
    {
      key                 = "gov:cms:inventory:Role"
      value               = "app server"
      propagate_at_launch = true
    },
    {
      key                 = "deploy"
      value               = "app"
      propagate_at_launch = true
    },
    {
      key                 = "gov:cms:inventory:Name"
      value               = "${var.vpc_name}-app"
      propagate_at_launch = true
    },
    {
      key                 = "gov:cms:inventory:Application"
      propagate_at_launch = true
      value               = "QPP"
    },
  ]

  user_data             = "${data.template_cloudinit_config.app_config.rendered}"
  vpc_cidr              = "${var.vpc_cidr}"
  vpc_id                = "${var.vpc_id}"
  vpc_name              = "${var.vpc_name}"

  wait_for_elb_capacity = "${var.wait_for_elb_capacity}"
}
