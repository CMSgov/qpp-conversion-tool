data "aws_ssm_parameter" "conversiontool_pager_duty_high_key" {
  name = "/global/conversiontool_pager_duty_high_key"
}

data "aws_ssm_parameter" "conversiontool_pager_duty_low_key" {
  name = "/global/conversiontool_pager_duty_low_key"
}

data "aws_ssm_parameter" "new_relic_api_key" {
  name = "/global/conversiontool_newrelic_key"
}

data "newrelic_alert_policy" "pagerduty_alerts_critical" {
  name = "QPPSF Conversion Tool ${var.environment} Pagerduty Critical"
}

terraform {
  required_providers {
    newrelic = {
      source  = "newrelic/newrelic"
      version = "2.49.0"
    }
  }
}

# Configure the New Relic provider
provider "newrelic" {
  account_id = 3519587
  api_key = data.aws_ssm_parameter.new_relic_api_key.value
  region = "US"
}

resource "newrelic_synthetics_monitor" "synthetic_ping_monitor" {
  name      = "${var.application}-${var.environment}-healthcheck"
  type      = "SIMPLE"
  frequency = "1"
  status    = "ENABLED"
  locations = ["AWS_US_EAST_1", "AWS_US_EAST_2"]
  uri       = var.ct_api_url

  verify_ssl                = false
  bypass_head_request       = true
  treat_redirect_as_failure = true
}

resource "newrelic_synthetics_alert_condition" "synthetic_alerts" {
  policy_id = data.newrelic_alert_policy.pagerduty_alerts_critical.id

  name        = "QPPSF Conversion Tool ${var.environment} Health Check"
  monitor_id  = newrelic_synthetics_monitor.synthetic_ping_monitor.id
}