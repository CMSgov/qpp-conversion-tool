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
        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })
}
