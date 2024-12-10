#IAM Role to Integrate AWS with New Relic for Sending Metrics
terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/qppsf-ct-newrelic-integration-iam"
    region  = "us-east-1"
    encrypt = "true"
  }

    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=4.55.0"
        }
    }
    required_version = "1.5.0"
}

locals {
  myregion = "us-east-1"
}

provider "aws" {
  region  = local.myregion
}

data "aws_caller_identity" "current" {}

resource "aws_iam_role" "new_relic_role" {
  name                 = "NewRelicInfrastructure-Integrations-CT"
  path                 = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/cms-cloud-admin/developer-boundary-policy"

  assume_role_policy = <<EOF
{
      "Version": "2012-10-17",
      "Statement": [
          {
              "Effect": "Allow",
              "Principal": { "AWS": "754728514883" },
              "Action": "sts:AssumeRole",
              "Condition": {
                  "StringEquals": {
                      "sts:ExternalId": "3519587"
                  }
              }
          }
      ]
  }
EOF
}

resource "aws_iam_policy" "new_relic_budget_policy" {
  name        = "NewRelic-Budget-Policy"
  path        = "/delegatedadmin/developer/"

 policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["budgets:ViewBudget"],
      "Resource": "arn:aws:${local.myregion}:*:*:*"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "read_only_policy" {
  policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"
  role       = aws_iam_role.new_relic_role.name
}

resource "aws_iam_role_policy_attachment" "budget_policy" {
  role       = aws_iam_role.new_relic_role.name
  policy_arn = aws_iam_policy.new_relic_budget_policy.arn
}
