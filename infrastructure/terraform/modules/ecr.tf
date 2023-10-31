# QPPSE-1208
locals {
  ctecr_tags = {
    Name                = "${var.project_name}-ecr-${var.environment}"
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = "Application"
    qpp_sensitivity     = var.sensitivity
    qpp_description     = "ECR Repo for Conversiontool"
    qpp_iac-repo-url    = var.git-origin
  }
}

resource "aws_ecr_repository" "qpp-final-scoring-ct" {
  name                 = "qppsf/conversion-tool/${var.environment}"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = merge(var.tags,local.ctecr_tags)
}

resource "aws_ecr_repository_policy" "qpp-ecr-repository" {
  repository = aws_ecr_repository.qpp-final-scoring-ct.name

  policy = <<EOF
{
    "Version": "2008-10-17",
    "Statement": [
        {
            "Sid": "qppfs-ecr-policy",
            "Effect": "Allow",
            "Principal": "*",
            "Action": [
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "ecr:BatchCheckLayerAvailability",
                "ecr:PutImage",
                "ecr:InitiateLayerUpload",
                "ecr:UploadLayerPart",
                "ecr:CompleteLayerUpload",
                "ecr:DescribeRepositories",
                "ecr:GetRepositoryPolicy",
                "ecr:ListImages",
                "ecr:DeleteRepository",
                "ecr:BatchDeleteImage",
                "ecr:SetRepositoryPolicy",
                "ecr:DeleteRepositoryPolicy"
            ]
        }
    ]
}
EOF
}
