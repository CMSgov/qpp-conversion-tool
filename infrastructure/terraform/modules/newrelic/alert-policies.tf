data "aws_ssm_parameter" "conversiontool_pager_duty_high_key" {
  name = "/global/conversiontool_pager_duty_high_key"
}

data "aws_ssm_parameter" "conversiontool_pager_duty_low_key" {
  name = "/global/conversiontool_pager_duty_low_key"
}

resource "newrelic_alert_policy" "pagerduty_alerts_critical" {
  name = "QPPSF Conversion Tool ${var.environment} Pagerduty Critical"
  incident_preference = "PER_CONDITION_AND_TARGET"
}

resource "newrelic_alert_policy" "pagerduty_alerts_noncritical" {
  name = "QPPSF Conversion Tool ${var.environment} Pagerduty Low"
  incident_preference = "PER_CONDITION_AND_TARGET"
}

# New Relic PagerDuty Notification Alerts - Critical
resource "newrelic_alert_channel" "pagerduty_critical" {
  name = "QPPSF Conversion Tool ${var.environment} Health Check - Critical"
  type = "pagerduty"

  config {
    service_key = data.aws_ssm_parameter.conversiontool_pager_duty_high_key.value
  }

  lifecycle {
    ignore_changes = [config]
  }
}

resource "newrelic_alert_policy_channel" "pagerduty_critical_policy" {
  policy_id = "${newrelic_alert_policy.pagerduty_alerts_critical.id}"

  channel_ids = [
    newrelic_alert_channel.pagerduty_critical.id
  ]
}

# New Relic PagerDuty Notification Alerts - Non-Critical
resource "newrelic_alert_channel" "pagerduty_noncritical" {
  name = "QPPSF Conversion Tool ${var.environment} Health Check - Low"
  type = "pagerduty"

  config {
    service_key = data.aws_ssm_parameter.conversiontool_pager_duty_low_key.value
  }

  lifecycle {
    ignore_changes = [config]
  }
}

resource "newrelic_alert_policy_channel" "pagerduty_noncritical_policy" {
  policy_id = "${newrelic_alert_policy.pagerduty_alerts_noncritical.id}"

  channel_ids = [
    newrelic_alert_channel.pagerduty_noncritical.id
  ]
}