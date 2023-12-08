terraform {
    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=4.55.0"
        }
    }
    required_version = "1.5.0"
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

resource "aws_iam_openid_connect_provider" "github_openid" {
  url = var.git-provider-url
  client_id_list = ["sts.amazonaws.com"]
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]
}

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
          Federated = aws_iam_openid_connect_provider.github_openid.arn
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
			"Resource": "arn:aws:ecs:${var.region}:*:*"
		},
    {
        "Action": [
            "acm:ListCertificates",
            "acm:ExportCertificate",
            "acm:GetCertificate",
            "acm:DescribeCertificate"
        ],
        "Effect": "Allow",
        "Resource": ["arn:aws:acm:${var.region}:${data.aws_caller_identity.current.account_id}:certificate/*"],
        "Sid": "ACMPermissions"
    },
    {
        "Sid": "ECRauthorization",
        "Effect": "Allow",
        "Action": "ecr:GetAuthorizationToken",
        "Resource": "arn:aws:ecr:${var.region}:*:*"
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
            "003384571330.dkr.ecr.us-east-1.amazonaws.com/qppsf/conversion-tool/dev",
            "003384571330.dkr.ecr.us-east-1.amazonaws.com/qppsf/conversion-tool/devpre",
            "003384571330.dkr.ecr.us-east-1.amazonaws.com/qppsf/conversion-tool/impl",
            "003384571330.dkr.ecr.us-east-1.amazonaws.com/qppsf/conversion-tool/prod"
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
        "Resource": ["arn:aws:ssm:${var.region}:${data.aws_caller_identity.current.account_id}:parameter/qppar-sf/*"],
        "Sid": "SSMPermissions"
    }
	]
})
}

resource "aws_iam_role_policy_attachment" "github_actions" {
  role       = aws_iam_role.github_action_conversiontool_role.name
  policy_arn = aws_iam_policy.github_actions_conversiontool_policy.arn
}