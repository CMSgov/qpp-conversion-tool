terraform {
    required_version = "0.13.7"
    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "=3.52.0"
        }
    }
}

provider "aws" {
    region = "us-east-1"
}

terraform {
  backend "s3" {
    bucket  = "qppsf-conversion-tool-tf-state"
    key     = "qppsf/qppsf-iam-ecs-gh-tf-state"
    region  = "us-east-1"
    encrypt = "true"
  }
}

data "aws_caller_identity" "current" {}


resource "aws_iam_user" "github-actions-ecr" {
	name = "github-actions-ecr"
}

#IAM policy to describe task definition
resource "aws_iam_user_policy" "ecsgithub" {
  name = "ecs-github-describetask"
  user = aws_iam_user.github-actions-ecr.name

policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        "Sid": "githubecs",
        Action = [
          "ecs:DescribeTaskDefinition",
        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })
}

