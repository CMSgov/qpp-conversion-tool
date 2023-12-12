data "aws_ssm_parameter" "conversiontool_pager_duty_high_key" {
  name = "/global/conversiontool_pager_duty_high_key"
}

data "aws_ssm_parameter" "conversiontool_pager_duty_low_key" {
  name = "/global/conversiontool_pager_duty_low_key"
}

data "aws_ssm_parameter" "new_relic_api_key" {
  name = "/global/conversiontool_newrelic_key"
}

terraform {
  required_providers {
    newrelic = {
      source  = "newrelic/newrelic"
      version = "3.25.2"
    }
  }
}

# Configure the New Relic provider
provider "newrelic" {
  account_id = 3519587
  api_key = data.aws_ssm_parameter.new_relic_api_key.value
  region = "US"
}

