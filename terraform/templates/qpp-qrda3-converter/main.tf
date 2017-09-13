module "open_http" {
  source = "git@github.com:CMSgov/corevpc//terraform/modules/aws/util/security-groups/open-http?ref=6ad748bcec4adc91a3d7197db0af68d0726f6207"

  vpc_id   = "${var.vpc_id}"
  vpc_name = "${var.vpc_name}"
}

module "route53_hosted_zone" {
  source    = "git@github.com:CMSgov/corevpc//terraform/modules/aws/route53/hosted_zone?ref=732a2de09ee70785f0803c044f0906c64392463f"
  vpc_id    = "${var.vpc_id}"
  zone_name = "${var.vpc_name}.hcgov.internal"
  stack_tag = "${var.stack_tag}"
}

module "system_user_data" {
  source    = "git@github.com:CMSgov/corevpc//terraform/modules/corevpc/system_user_data?ref=71b7299b8a8676a7bfcb3426ff166ddcc1431397"
  s3_bucket = "${var.s3_bucket}"
  vpc_name  = "${var.vpc_name}"
}
