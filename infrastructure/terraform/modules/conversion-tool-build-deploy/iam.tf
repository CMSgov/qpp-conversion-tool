data "aws_caller_identity" "current" {}

locals {
    account_id = data.aws_caller_identity.current.account_id
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