############################################################# CodeBuild ###############################################
resource "aws_codebuild_webhook" "codebuild_webhook_event" {
  project_name = aws_codebuild_project.conversion_tool_codebuild_project.name
  build_type   = "BUILD"
  filter_group {
    filter {
      type    = "EVENT"
      pattern = "PUSH"
    }

    filter {
      type    = "HEAD_REF"
      pattern = var.codebuild_branch_ref
    }
  }
}

resource "aws_codebuild_project" "conversion_tool_codebuild_project" {
  name          = "${var.team}-${var.environment}-conversiontool-codebuild-ecs-deploy"
  description   = "CodeBuild project for ConversionTool, ECR Publish and ECS Deploy"
  build_timeout = "120"
  service_role  = "${aws_iam_role.conversiontool_codebuild_servicerole.arn}"
    

  artifacts {
    type = "NO_ARTIFACTS"
  }


  source {
    type            = "GITHUB"
    location        = var.git-origin
    git_clone_depth = 1
    buildspec       = "./buildspec.yml"

    git_submodules_config {
      fetch_submodules = true
    }
  }

  environment {
    image                       = "aws/codebuild/standard:5.0"
    type                        = "LINUX_CONTAINER"
    compute_type                = "BUILD_GENERAL1_MEDIUM"
    image_pull_credentials_type = "CODEBUILD"
    privileged_mode             = true

    environment_variable {
      name  = "ENVIRONMENT"
      value = var.environment
    }
    environment_variable {
      name  = "CERT_CP_PATH"
      value = "rest-api/src/main/resources/"
    }
    environment_variable {
      name  = "ecs_container_name"
      value = "conversion-tool"
    }
  }

  logs_config {
    cloudwatch_logs {
      group_name  = "${var.project_name}-conversiontool-codebuild-${var.environment}-log-group"
      stream_name = "${var.project_name}-conversiontool-codebuild-${var.environment}-log-stream"
    }

    s3_logs {
      status = "DISABLED"
    }
  }
}