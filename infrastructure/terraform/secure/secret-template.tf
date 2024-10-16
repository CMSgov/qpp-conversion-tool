resource "aws_ssm_parameter" "db_app_password" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DB_APP_PASSWORD"
  description = "DB_APP_PASSWORD"
  type        = "SecureString"
  value       = "secret"
  overwrite   = false

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
  overwrite   = false

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
  overwrite   = false

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
  overwrite   = false

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

resource "aws_ssm_parameter" "impl_aca_cookie" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/IMPL_ACA_COOKIE"
  description = "IMPL_ACA_COOKIE"
  type        = "SecureString"
  value       = "secret"
  overwrite   = false

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
  overwrite   = false

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
  overwrite   = false

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
