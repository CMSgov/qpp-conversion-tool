data "aws_caller_identity" "current" {}

data "aws_iam_policy" "AmazonDynamoDBFullAccess" {
  arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
}

data "aws_iam_policy" "AmazonECSTaskExecutionRolePolicy" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_task_exec_role" {
  name = "${var.project_name}-ecstaskexecution-role"
  description          = "Allows ECS tasks to call AWS services on your behalf."
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
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy" "ecspolicypermissions" {
  name = "ecspermissions"
  path = "/delegatedadmin/developer/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ecspermissions",
            "Effect": "Allow",
            "Action": [
                "s3:GetAccessPoint",
                "ssm:DescribeDocument",
                "kms:GenerateRandom",
                "ec2messages:GetEndpoint",
                "ssmmessages:OpenControlChannel",
                "ec2messages:GetMessages",
                "ssm:PutConfigurePackageResult",
                "ssm:ListInstanceAssociations",
                "ssm:GetParameter",
                "ssm:UpdateAssociationStatus",
                "ssm:GetManifest",
                "kms:DescribeCustomKeyStores",
                "kms:DeleteCustomKeyStore",
                "ec2messages:DeleteMessage",
                "ssm:UpdateInstanceInformation",
                "kms:UpdateCustomKeyStore",
                "ec2messages:FailMessage",
                "ssmmessages:OpenDataChannel",
                "ssm:GetDocument",
                "kms:CreateKey",
                "kms:ConnectCustomKeyStore",
                "s3:HeadBucket",
                "ssm:PutComplianceItems",
                "ssm:DescribeAssociation",
                "s3:PutAccountPublicAccessBlock",
                "ssm:GetDeployablePatchSnapshotForInstance",
                "s3:ListAccessPoints",
                "s3:ListJobs",
                "ec2messages:AcknowledgeMessage",
                "ssm:GetParameters",
                "ssmmessages:CreateControlChannel",
                "kms:CreateCustomKeyStore",
                "ssmmessages:CreateDataChannel",
                "kms:ListKeys",
                "ssm:PutInventory",
                "s3:GetAccountPublicAccessBlock",
                "s3:ListAllMyBuckets",
                "kms:ListAliases",
                "kms:DisconnectCustomKeyStore",
                "ec2messages:SendReply",
                "s3:CreateJob",
                "ssm:ListAssociations",
                "ssm:UpdateInstanceAssociationStatus"
            ],
            "Resource": "*"
        },
        {
            "Sid": "kmspermissions",
            "Effect": "Allow",
            "Action": "kms:*",
            "Resource": "arn:aws:kms:*:*:key/*"
        },
        {
            "Sid": "s3permissions",
            "Effect": "Allow",
            "Action": "s3:*",
            "Resource": [
                "arn:aws:s3:::*",
                "arn:aws:s3:*:*:accesspoint/*",
                "arn:aws:s3:::*/*",
                "arn:aws:s3:*:*:job/*"
            ]
        }
    ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "dynamodbfullaccess-policy-attach" {
  role       = "${aws_iam_role.ecs_task_exec_role.name}"
  policy_arn = "${data.aws_iam_policy.AmazonDynamoDBFullAccess.arn}"
}

resource "aws_iam_role_policy_attachment" "ecastaskexecution-policy-attach" {
  role       = "${aws_iam_role.ecs_task_exec_role.name}"
  policy_arn = "${data.aws_iam_policy.AmazonECSTaskExecutionRolePolicy.arn}"
}

resource "aws_iam_role_policy_attachment" "ecspolicypermissions-attach" {
  role       = aws_iam_role.ecs_task_exec_role.name
  policy_arn = aws_iam_policy.ecspolicypermissions.arn
}
