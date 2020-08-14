# Add application variables to SSM, values are created manually, the ones below are placeholders for creating the location in SSM. 
# Re-running this terraform should not overwrite them as "overwrite" defaults to false
# *Uses default KMS key*

resource "aws_ssm_parameter" "app_env" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/APP_ENV"
  description = "APP_ENV"
  type        = "String"
  value       = "dev"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }

}

resource "aws_ssm_parameter" "ar_api_base_url" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/AR_API_BASE_URL"
  description = "AR_API_BASE_URL"
  type        = "String"
  value       = "https://dev.ar.qpp.internal/api/v1/fms/file/qpp_data/qppct/testCpcPlusValidationFile.json"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "bucket_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/BUCKET_NAME"
  description = "BUCKET_NAME"
  type        = "String"
  value       = "aws-hhs-cms-ccsq-qpp-navadevops-pii-convrtr-audt-dev-us-east-1"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "cpc_end_date" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_END_DATE"
  description = "CPC_END_DATE"
  type        = "String"
  value       = "2020-03-13 - 20:00:00"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "cpc_plus_bucket_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_PLUS_BUCKET_NAME"
  description = "CPC_PLUS_BUCKET_NAME"
  type        = "String"
  value       = "aws-hhs-cms-ccsq-qpp-navadevops-pii-cnvrt-npicpc-dev-us-east-1"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "cpc_plus_unprocessed_filter_start_date" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_PLUS_UNPROCESSED_FILTER_START_DATE"
  description = "CPC_PLUS_UNPROCESSED_FILTER_START_DATE"
  type        = "String"
  value       = "2020-01-02T04:59:59.999Z"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "cpc_plus_validation_file" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_PLUS_VALIDATION_FILE"
  description = "CPC_PLUS_VALIDATION_FILE"
  type        = "String"
  value       = "testCpcPlusValidationFile.json"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "db_app_password" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DB_APP_PASSWORD"
  description = "DB_APP_PASSWORD"
  type        = "SecureString"
  value       = "secret"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "db_app_username" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DB_APP_USERNAME"
  description = "DB_APP_USERNAME"
  type        = "String"
  value       = "app"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "db_master_password" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DB_MASTER_PASSWORD"
  description = "DB_MASTER_PASSWORD"
  type        = "SecureString"
  value       = "secret"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "db_master_username" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DB_MASTER_USERNAME"
  description = "DB_MASTER_USERNAME"
  type        = "String"
  value       = "supersuit"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "deploy0a_public-i-p" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DEPLOY0A_PUBLIC-I-P"
  description = "DEPLOY0A_PUBLIC-I-P"
  type        = "String"
  value       = "34.198.65.93"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "dynamo_table_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DYNAMO_TABLE_NAME"
  description = "DYNAMO_TABLE_NAME"
  type        = "String"
  value       = "qpp-qrda3converter-dev-metadata"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "fms_token" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/FMS_TOKEN"
  description = "FMS_TOKEN"
  type        = "SecureString"
  value       = "secret"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "gdit_nessus_pub_key" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/GDIT_NESSUS_PUB_KEY"
  description = "GDIT_NESSUS_PUB_KEY"
  type        = "String"
  value       = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDLu1Q+kvJSbTD7cKHPwZ9aW3qb0SISHDt3EzEvBTUhGGYYLzG2FFUIkjTd1cc8OyJ9DNlrujIGTNmLjbdQ//F8CtH5990oWN7MtcM0BToTgJ9yhEvhZ7iq2YYxANTBNL6wiTWkS70a6bjsqHGWYJ9jvduGftzHVMYkkapl/oVysRJaNu+38B0Z0FXNmoorlO74/Rt7XK5MhcGbN0z4/1urEWlSl9ygHA4umWw2OM17F6NAEY9fM9W+hcZsK0SzQsFYNI0g1JADKgnXBYOmuPeg/M/6wKctHSiZBporprZ8h7sgK8Sts8Gc/loBVp8DkmnC/eZzi0MnZ4esy6mldW5"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "java_opts" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/JAVA_OPTS"
  description = "JAVA_OPTS"
  type        = "String"
  value       = "-Xms6G -Xmx6G -Xmn5G -XX:+UseStringDeduplication -XX:-AggressiveOpts"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "kms_key" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/KMS_KEY"
  description = "KMS_KEY"
  type        = "String"
  value       = "arn:aws:kms:us-east-1:003384571330:alias/qpp-qrda3converter-dev-kms_alias"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "newrelic_api_key" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/NEWRELIC_API_KEY"
  description = "NEWRELIC_API_KEY"
  type        = "SecureString"
  value       = "secret"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "nexus_creds" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/NEXUS_CREDS"
  description = "NEXUS_CREDS"
  type        = "SecureString"
  value       = "secret"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "nexus_host" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/NEXUS_HOST"
  description = "NEXUS_HOST"
  type        = "String"
  value       = "ec2-52-201-239-45.compute-1.amazonaws.com"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "org_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/ORG_NAME"
  description = "ORG_NAME"
  type        = "String"
  value       = "cpc-plus-conversion-tool"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "splunk_token" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/SPLUNK_TOKEN"
  description = "SPLUNK_TOKEN"
  type        = "SecureString"
  value       = "secret"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_ssm_parameter" "validation_url" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/VALIDATION_URL"
  description = "VALIDATION_URL"
  type        = "String"
  value       = "https://preview.qpp.cms.gov/api/submissions/public/validate-submission"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  tags = {
    Name            = "${var.project_name}-ssm-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}
