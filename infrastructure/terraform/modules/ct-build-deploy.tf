############################################################# CodePipeline ###############################################
data "aws_ssm_parameter" "conversiontool_git_pat" {
  name = "/qppar-sf/common/conversion_tool/GIT_PAT"
}

data "aws_ssm_parameter" "conversion_tool_artifact_s3" {
  name = "/qppar-sf/common/conversion_tool/codepipeline_artifact_bucket"
}

resource "aws_codestarconnections_connection" "ct_github_repo" {
  name          = "qppsf-conversiontool-github"
  provider_type = "GitHub"
}

resource "aws_codepipeline" "conversion_tool_pipeline" {
  name     = "${var.team}-${var.environment}-ecs-conversiontool-codepipeline"
  role_arn = "${aws_iam_role.conversiontool_codepipeline_role.arn}"

  artifact_store {
    location = data.aws_ssm_parameter.conversion_tool_artifact_s3.value
    type     = "S3"
  }

  stage {
    name = "Source"

    action {
      name             = "Source"
      category         = "Source"
      owner            = "AWS"
      provider         = "CodeStarSourceConnection"
      version          = "1"
      output_artifacts = ["source"]

      configuration = {
        ConnectionArn    = aws_codestarconnections_connection.ct_github_repo.arn
        FullRepositoryId = var.git-origin
        BranchName       = var.codebuild_branch_ref
      }
    }
  }

  stage {
    name = "Build"

    action {
      name             = "Build"
      category         = "Build"
      owner            = "AWS"
      provider         = "CodeBuild"
      version          = "1"
      input_artifacts  = ["source"]
      output_artifacts = ["build"]

      configuration = {
          ProjectName = "${aws_codebuild_project.conversion_tool_codebuild_project.name}"
      }
    }
  }
}


############################################################# CodeBuild ###############################################

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

############################################################# CodeDeploy ###############################################

resource "aws_codedeploy_app" "conversion_tool_ecs_codedeploy" {
  compute_platform = "ECS"
  name             = "${var.team}-${var.environment}-conversiontool-ecs-codedeploy"
}

resource "aws_codedeploy_deployment_group" "this" {
  app_name               = "${aws_codedeploy_app.this.name}"
  deployment_group_name  = "example-deploy-group"
  deployment_config_name = "CodeDeployDefault.ECSAllAtOnce"
  service_role_arn       = "${aws_iam_role.conversiontool_codedeploy_role.arn}"

  blue_green_deployment_config {
    deployment_ready_option {
      action_on_timeout = "CONTINUE_DEPLOYMENT"
    }

    terminate_blue_instances_on_deployment_success {
      action = "TERMINATE"
    }
  }

  ecs_service {
    cluster_name = "${aws_ecs_cluster.this.name}"
    service_name = "${aws_ecs_service.this.name}"
  }

  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "BLUE_GREEN"
  }

  load_balancer_info {
    target_group_pair_info {
      prod_traffic_route {
        listener_arns = ["${aws_lb_listener.this.arn}"]
      }

      target_group {
        name = "${aws_lb_target_group.this.*.name[0]}"
      }

      target_group {
        name = "${aws_lb_target_group.this.*.name[1]}"
      }
    }
  }
}
