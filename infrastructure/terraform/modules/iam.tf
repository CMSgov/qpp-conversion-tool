data "aws_caller_identity" "current" {}

resource "aws_iam_role" "ecs_task_execution_role" {
  name                 = "${var.project_name}-ecstask-role-${var.environment}"
  description          = "Conversion Tool ECS Task Execution Role"
  path                 = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/cms-cloud-admin/developer-boundary-policy"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": "ECSAccess"
    }
  ]
}
EOF

}

# IAM Role permissions to allow Cloudwatch logs to write to Kinesis Stream
resource "aws_iam_role" "cwlogs_to_kinesis" {
  name = "${var.project_name}-cloudwatch-to-kinesis-${var.environment}"
  path = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/cms-cloud-admin/developer-boundary-policy"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowCloudwatchtoKinesis",
      "Effect": "Allow",
      "Principal": {
        "Service": "logs.us-east-1.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "cwlogs_to_kinesis_policy" {
  name = "cloudwatch-to-kinesis-${var.environment}"
  path = "/delegatedadmin/developer/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "kinesis:DescribeStream",
                "kinesis:GetShardIterator",
                "kinesis:GetRecords",
                "kinesis:ListStreams",
                "kinesis:PutRecords",
                "kinesis:PutRecord",
                "kinesis:ListShards",
                "kinesis:DescribeStreamSummary",
                "kinesis:RegisterStreamConsumer"
            ],
            "Resource": "${aws_kinesis_stream.kinesis-stream-cw-logs.arn}"
        }
    ]
}
EOF
}

resource "aws_iam_role" "kinesis_lambda_role" {
  name = "${var.project_name}-kinesis_lambda_role-${var.environment}"
  path = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/cms-cloud-admin/developer-boundary-policy"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": "AllowLambda"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "kinesis_lambda_policy_role" {
  name = "conversiontool_kinesis_lambda_role_policy-${var.environment}"
  path = "/delegatedadmin/developer/"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "kinesis:DescribeStream",
                "kinesis:GetShardIterator",
                "kinesis:GetRecords",
                "kinesis:ListStreams",
                "kinesis:PutRecords"
            ],
            "Resource": [
              "${aws_kinesis_stream.kinesis-stream-cw-logs.arn}"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
                
            ],
            "Resource": [
                "arn:aws:logs:*:*:*"
            ]
        }
    ]
}
EOF
}


resource "aws_iam_role_policy_attachment" "kinesis_lambda_policy" {
  role       = aws_iam_role.kinesis_lambda_role.name
  policy_arn = aws_iam_policy.kinesis_lambda_policy_role.arn
}

resource "aws_iam_role_policy_attachment" "cwlogs_to_kinesis_policy" {
  role       = aws_iam_role.cwlogs_to_kinesis.name
  policy_arn = aws_iam_policy.cwlogs_to_kinesis_policy.arn
}

# ECS Task Execution IAM Role & Policy

resource "aws_iam_policy" "ct_ecsTaskExecution_policy" {
  name = "${var.project_name}-ecsTaskExecution-${var.environment}"
  path = "/delegatedadmin/developer/"
  policy = data.template_file.ecs_TaskExecution_policy.rendered

}

data "template_file" "ecs_TaskExecution_policy" {
  template = file("${path.module}/templates/ecs_task_execution_policy.tpl")

  vars = {
    env = var.environment
  }
}

resource "aws_iam_role_policy_attachment" "conversiontool_dynamodb" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
}

resource "aws_iam_role_policy_attachment" "conversiontool_ecs" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "conversiontool_ecstaskExecution" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = aws_iam_policy.ct_ecsTaskExecution_policy.arn
}

resource "aws_iam_role_policy_attachment" "ecs_instance_role_attachment" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}