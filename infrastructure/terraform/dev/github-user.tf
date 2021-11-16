# only create the user in dev - used by all environments
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
    ]
  })
}

# Added policy permissions on S3 artifacts bucket
resource "aws_iam_user_policy" "es3permissonsforgh" {
  name = "ecs-s3-bucket"
  user = aws_iam_user.github-actions-ecr.name

policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        "Sid": "s3ecsiam",
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
    ]
  })
}

# Added policy permissions to retrieve parameter from SSM
resource "aws_iam_user_policy" "ssmpermissionsforgh" {
  name = "ecs-ssm"
  user = aws_iam_user.github-actions-ecr.name

policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        "Sid": "s3ecsiam",
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