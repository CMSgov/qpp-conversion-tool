data "aws_caller_identity" "current" {}

locals {
    account_id = data.aws_caller_identity.current.account_id
}

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


################################################# CodeBuild IAM Role ######################################

resource "aws_iam_role" "conversiontool_codebuild_servicerole" {
  name = "${var.team}-${var.environment}-codebuild-servicerole-conversiontool"
  path = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${local.account_id}:policy/cms-cloud-admin/developer-boundary-policy"
 assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "codebuild.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": "AllowCodeBuild"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "conversiontool_svc_policy" {
  name = "${var.team}-${var.environment}-conversiontool-codebuildservice-role-policy"
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
			"Sid": "AllowS3",
			"Effect": "Allow",
			"Action": [
				"s3:GetObject",
				"s3:ListBucket",
				"s3:PutObject"
			],
			"Resource": [
				"arn:aws:s3:::qppsf-codepipeline-s3-003384571330-us-east-1",
				"arn:aws:s3:::qppsf-codepipeline-s3-003384571330-us-east-1/*"
			]
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
			"Sid": "CloudWatchLogsPolicy",
			"Effect": "Allow",
			"Action": [
				"logs:CreateLogGroup",
				"logs:CreateLogStream",
				"logs:PutLogEvents"
			],
			"Resource": "arn:aws:logs:*:*:*"
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
			"Resource": [
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/new-qpp-conversion-tool",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/dev",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/devpre",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/impl",
				"arn:aws:ecr:us-east-1:${local.account_id}:repository/qppsf/conversion-tool/prod"
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

resource "aws_iam_role_policy_attachment" "conversiontool_servicerole_policyattachment" {
  role       = aws_iam_role.conversiontool_codebuild_servicerole.name
  policy_arn = aws_iam_policy.conversiontool_svc_policy.arn
}


################################################# CodePipeline IAM Role ######################################

resource "aws_iam_role" "conversiontool_codepipeline_role" {
  name = "${var.team}-${var.environment}-codepipeline-conversiontool-role"
  path = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${local.account_id}:policy/cms-cloud-admin/developer-boundary-policy"
 assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "codepipeline.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": "AllowCodePipeline"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "conversiontool_servicerole_policy" {
  name = "${var.team}-${var.environment}-conversiontool-codepipeline-role-policy"
  path = "/delegatedadmin/developer/"
  policy = jsonencode({
	"Version": "2012-10-17",
    "Statement": [{
        "Sid": "AllowS3",
		"Effect": "Allow",
		"Action": [
			"s3:GetObject",
            "s3:ListBucket",
            "s3:PutObject"
		],
		"Resource": [
            "arn:aws:s3:::qppsf-codepipeline-s3-003384571330-us-east-1",
      		"arn:aws:s3:::qppsf-codepipeline-s3-003384571330-us-east-1/*"
        ]
    },
	{
    	"Effect": "Allow",
        "Action": "codestar-connections:UseConnection",
        "Resource": "${aws_codestarconnections_connection.ct_github_repo.arn}"
	},
    {
      	"Sid": "AllowCodeBuild",
    	"Effect": "Allow",
    	"Action": [
      		"codebuild:BatchGetBuilds",
            "codebuild:StartBuild"
    	],
		"Resource": [
      		"${aws_codebuild_project.conversion_tool_codebuild_project.arn}"
    	]
  	},
    {
    	"Sid": "AllowCodeDeploy",
    	"Effect": "Allow",
    	"Action": [
      		"codedeploy:CreateDeployment",
            "codedeploy:GetApplication",
            "codedeploy:GetApplicationRevision",
            "codedeploy:GetDeployment",
            "codedeploy:GetDeploymentConfig",
            "codedeploy:RegisterApplicationRevision"
    	],
		"Resource": ["*"]
  	},
    {
    	"Sid": "AllowECS",
    	"Effect": "Allow",
    	"Action": [ 
            "ecs:*",
            "iam:PassRole"
            ],
		"Resource": ["*"]
  	}
	]
})
}

resource "aws_iam_role_policy_attachment" "conversiontool_codepipeline_policyattachment" {
  role       = aws_iam_role.conversiontool_codepipeline_role.name
  policy_arn = aws_iam_policy.conversiontool_servicerole_policy.arn
}

################################################# CodeDeploy IAM Role ######################################

resource "aws_iam_role" "conversiontool_codedeploy_role" {
  name = "${var.team}-${var.environment}-codedeploy-conversiontool-role"
  path = "/delegatedadmin/developer/"
  permissions_boundary = "arn:aws:iam::${local.account_id}:policy/cms-cloud-admin/developer-boundary-policy"
 assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "codedeploy.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": "AllowCodedeploy"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "conversiontool_codedeploy_policy" {
  name = "${var.team}-${var.environment}-conversiontool-codedeploy-policy"
  path = "/delegatedadmin/developer/"
  policy = jsonencode({
  "Version": "2012-10-17",
  "Statement": [{
      "Sid": "AllowLoadBalancingAndECSModifications",
      "Effect": "Allow",
      "Action": [
            "ecs:CreateTaskSet",
            "ecs:DeleteTaskSet",
            "ecs:DescribeServices",
            "ecs:UpdateServicePrimaryTaskSet",
            "elasticloadbalancing:DescribeListeners",
            "elasticloadbalancing:DescribeRules",
            "elasticloadbalancing:DescribeTargetGroups",
            "elasticloadbalancing:ModifyListener",
            "elasticloadbalancing:ModifyRule"
        ],

            "Resource": ["*"]
        },
        {
            "Sid":  "AllowS3",
            "Effect": "Allow",
      		"Action": ["s3:GetObject"],
            "Resource": [
        		"arn:aws:s3:::qppsf-codepipeline-s3-003384571330-us-east-1",
            	"arn:aws:s3:::qppsf-codepipeline-s3-003384571330-us-east-1/*"
      	]
        },
        {
         "Sid": "AllowPassRole",
         "Effect": "Allow",
      	 "Action": ["iam:PassRole"],
            "Resource": [
                "${aws_iam_role.ecs_task_execution_role.arn}"
            ]
        }
  	]
})
}

resource "aws_iam_role_policy_attachment" "codedeploy_policy_policyattachment" {
  role       = aws_iam_role.conversiontool_codedeploy_role.name
  policy_arn = aws_iam_policy.conversiontool_codedeploy_policy.arn
}