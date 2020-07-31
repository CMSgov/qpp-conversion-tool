resource "aws_iam_role" "qppsf_conversion_tool_ecs_role" {
  name = "qppsf_conversion_tool_ecs_role_${var.environment}"

  assume_role_policy = <<EOF
{
  "Version": "2008-10-17",
  "Statement": [
    {
      "Sid": "ECSAccess",
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "emr_ec2" {
  name        = "${var.environment}-${var.project_name}-emr-ec2"
  description = null

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowReadWriteToDynamoDb",
            "Effect": "Allow",
            "Action": "dynamodb:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Resource": "*",
            "Action": [
                "cloudwatch:*",
                "ec2:Describe*",
                "ec2:AssignPrivateIpAddresses",
                "elasticmapreduce:Describe*",
                "elasticmapreduce:ListBootstrapActions",
                "elasticmapreduce:ListClusters",
                "elasticmapreduce:ListInstanceGroups",
                "elasticmapreduce:ListInstances",
                "elasticmapreduce:ListSteps",
                "rds:Describe*",
                "sdb:*",
                "sns:*",
                "sqs:*"
            ]
        }
    ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "emr_ec2" {
  role       = aws_iam_role.qppsf_conversion_tool_ecs_role.name
  policy_arn = aws_iam_policy.emr_ec2.arn
}

resource "aws_iam_role_policy_attachment" "emr_ec2_s3_read" {
  role       = aws_iam_role.qppsf_conversion_tool_ecs_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "qppsf_conversion_tool_ecs_role" {
  name = "${var.environment}-${var.project_name}-emr-ec2-role"
  role = aws_iam_role.qppsf_conversion_tool_ecs_role.name
}
