data "template_cloudinit_config" "jump_config" {
  gzip          = true
  base64_encode = true

  part {
    content_type = "text/x-shellscript"
    content      = "${module.system_user_data.header}"
  }

  part {
    content_type = "text/x-shellscript"
    content      = "${module.jump_service_user_data.user_data}"
  }

  part {
    content_type = "text/x-shellscript"
    content      = "${module.system_user_data.footer}"
  }

  part {
    content_type = "text/x-shellscript"
    content      = "${module.jump_service_user_data.user_data_commands}"
  }
}

module "jump_service_user_data" {
  source = "git@github.com:CMSgov/corevpc//terraform/modules/corevpc/service_user_data?ref=2ce4c5a916a01c9bc9905b123728986da7dbff39"

  name = "deploy"

  startup_commands = ""
}

module "jump" {
  source = "git@github.com:CMSgov/corevpc//terraform/modules/aws/asg/jump?ref=6bffd68d64196e7e09e8053cb95c734c38456b90"

  ami_id               = "${var.app_ami_id}"
  iam_instance_profile = "${var.iam_instance_profile}"
  instance_type        = "${lookup(var.instance_types, "jump")}"
  key_name             = "${var.key_name}"
  subnet_ids           = ["${var.dmz_subnet_ids}"]
  user_data            = "${data.template_cloudinit_config.jump_config.rendered}"
  vpc_id               = "${var.vpc_id}"
  vpc_name             = "${var.vpc_name}"
}
