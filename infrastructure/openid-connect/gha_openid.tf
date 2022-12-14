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
    key     = "qppsf/conversion-tool-gha-openidconnect.tfstate"
    region  = "us-east-1"
    encrypt = "true"
  }
}

data "aws_caller_identity" "current" {}

resource "aws_iam_role" "github_action_conversiontool_role" {
  name = "${var.team}-${var.environment}-githubactions-conversiontool-role"
  path = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/cms-cloud-admin/developer-boundary-policy"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity",
        Effect = "Allow"
        Sid    = "GithubActionsPermissions",
        Principal = {
          Federated = "arn:aws:iam::003384571330:oidc-provider/token.actions.githubusercontent.com"
        }
        Condition = {
          StringLike = {
            "token.actions.githubusercontent.com:sub" = ["repo:${var.git-org}/${var.git-repo}:*"]
          }
        }
      },
    ]
  })
}

resource "aws_iam_policy" "github_actions_conversiontool_policy" {
  name = "${var.team}-${var.environment}-githubactions-conversiontool-policy"
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
            "Resource":[
                "arn:aws:ecr:us-east-1:003384571330:repository/new-qpp-conversion-tool",
                "arn:aws:ecr:us-east-1:003384571330:repository/qppsf/conversion-tool/dev",
                "arn:aws:ecr:us-east-1:003384571330:repository/qppsf/conversion-tool/devpre",
                "arn:aws:ecr:us-east-1:003384571330:repository/qppsf/conversion-tool/impl",
                "arn:aws:ecr:us-east-1:003384571330:repository/qppsf/conversion-tool/prod"

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

resource "aws_iam_role_policy_attachment" "github_actions" {
  role       = aws_iam_role.github_action_conversiontool_role.name
  policy_arn = aws_iam_policy.github_actions_conversiontool_policy.arn
}