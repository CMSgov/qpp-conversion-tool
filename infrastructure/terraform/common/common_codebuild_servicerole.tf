terraform {
    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=3.70.0"
        }
    }
    required_version = "1.0.0"
}

provider "aws" {
    region = "us-east-1"
}

terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/conversion-tool-codebuild_servicerole.tfstate"
    region  = "us-east-1"
    encrypt = "true"
  }
}

data "aws_caller_identity" "current" {}

locals {
    account_id = data.aws_caller_identity.current.account_id
}

resource "aws_iam_role" "conversiontool_codebuild_servicerole" {
  name = "${var.team}-${var.environment}-codebuild-servicerole-conversiontool-role"
  path = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${local.account_id}:policy/cms-cloud-admin/developer-boundary-policy"
 assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "codebuild.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": "AllowCodeBuild"
    }
  ]
}
EOF
}


resource "aws_iam_policy" "conversiontool_servicerole_policy" {
  name = "${var.team}-${var.environment}-conversiontool-codebuildservice-role-policy"
  path = "/delegatedadmin/developer/"
  policy = jsonencode({
	"Version": "2012-10-17",
	"Statement": [{
			"Sid": "githubecs",
			"Effect": "Allow",
			"Action": [
				"ecs:DescribeTaskDefinition",
				"ecs:RegisterTaskDefinition",
				"ecs:DescribeServices",
				"ecs:UpdateService",
				"iam:GetRole",
				"iam:PassRole"
			],
			"Resource": "*"
		},
		{
			"Action": [
				"acm:ListCertificates",
				"acm:ExportCertificate",
				"acm:GetCertificate",
				"acm:DescribeCertificate"
			],
			"Effect": "Allow",
			"Resource": "*",
			"Sid": "ACMPermissions"
		},
		{
			"Sid": "CloudWatchLogsPolicy",
			"Effect": "Allow",
			"Action": [
				"logs:CreateLogGroup",
				"logs:CreateLogStream",
				"logs:PutLogEvents"
			],
			"Resource": "arn:aws:logs:*:*:*"
		},
		{
			"Sid": "ECRauthorization",
			"Effect": "Allow",
			"Action": "ecr:GetAuthorizationToken",
			"Resource": "*"
		},
		{
			"Sid": "ECRPermissions",
			"Effect": "Allow",
			"Action": [
				"ecr:GetDownloadUrlForLayer",
				"ecr:BatchGetImage",
				"ecr:CompleteLayerUpload",
				"ecr:UploadLayerPart",
				"ecr:InitiateLayerUpload",
				"ecr:BatchCheckLayerAvailability",
				"ecr:PutImage"
			],
			"Resource": [
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/new-qpp-conversion-tool",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/dev",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/devpre",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/impl",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/prod"
			]
		},
		{
			"Action": [
				"ssm:GetParameters",
				"ssm:PutParameter",
				"ssm:GetParameterHistory",
				"ssm:GetParametersByPath",
				"ssm:GetParameter",
				"ssm:DescribeParameters"
			],
			"Effect": "Allow",
			"Resource": "*",
			"Sid": "SSMPermissions"
		}
	]
})
}

resource "aws_iam_role_policy_attachment" "conversiontool_servicerole_policyattachment" {
  role       = aws_iam_role.conversiontool_codebuild_servicerole.name
  policy_arn = aws_iam_policy.conversiontool_servicerole_policy.arn
}