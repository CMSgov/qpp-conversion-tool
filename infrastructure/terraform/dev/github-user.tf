# only create the user in dev - used by all environments
resource "aws_iam_user" "github-actions-ecr" {
  name = "github-actions-ecr"
}

resource "aws_iam_group" "github-actions-push" {
  name = "github-actions-push"
}

#IAM user Group Policy
resource "aws_iam_group_policy" "ecsgithub" {
  name = "ecs-github-describetask"
  group = aws_iam_group.github-actions-push.name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        "Sid" : "githubecs",
        Action = [
          "ecs:DescribeTaskDefinition",
          "ecs:RegisterTaskDefinition",
          "ecs:DescribeServices",
          "ecs:UpdateService",
          "iam:GetRole",
          "iam:PassRole"
        ]
        Effect   = "Allow"
        Resource = "*"
      },
      {
        "Sid": "S3SSLBUCKETPermissions",
        Action = [
          "s3:ListBucket",
          "s3:GetObject"
          
        ]
        Effect   = "Allow"
        Resource = [
          "arn:aws:s3:::qppsf-conversion-tool-artifacts-ssl-bucket",
          "arn:aws:s3:::qppsf-conversion-tool-artifacts-ssl-bucket/*"
        ]
      },
      {
        "Sid": "SSMPermissions",
        Action = [
          "ssm:GetParameters",
          "ssm:PutParameter",
          "ssm:GetParameterHistory",
          "ssm:GetParametersByPath",
          "ssm:GetParameter",
          "ssm:DescribeParameters"
          
        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })
}