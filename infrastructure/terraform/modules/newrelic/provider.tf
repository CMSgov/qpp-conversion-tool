data "aws_ssm_parameter" "new_relic_api_key" {
  name = "/global/conversiontool_newrelic_key"
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