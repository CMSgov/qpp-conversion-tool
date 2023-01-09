resource "aws_ecs_cluster" "conversion-tool-ecs-cluster" {
  name = "qppsf-conversion-tool-${var.environment}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }


  tags = {
    "Name"                = "${var.project_name}-ecs-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "ECS Cluster for Conversiontool"
    "qpp:iac-repo-url"    = var.git-origin
  }
}

resource "aws_ecs_task_definition" "conversion-tool" {
  family                   = "qppsf-conversion-tool-td-${var.environment}"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "2048"
  memory                   = "5120"
  task_role_arn            = "arn:aws:iam::003384571330:role/ecsTaskExecutionRole"

  container_definitions = data.template_file.ct_task_def.rendered
}

data "template_file" "ct_task_def" {
  template = file("${path.module}/templates/conversion_tool_task_def.tpl")

  vars = {
    env = var.environment
  }
}

resource "aws_ecs_service" "conversion-tool-service" {
  name                               = "conversion-tool-service-${var.environment}"
  cluster                            = aws_ecs_cluster.conversion-tool-ecs-cluster.id
  task_definition                    = aws_ecs_task_definition.conversion-tool.arn
  desired_count                      = 1
  launch_type                        = "FARGATE"
  deployment_maximum_percent         = "100"
  deployment_minimum_healthy_percent = "0"
  platform_version                   = "1.4.0"

  lifecycle {
    ignore_changes = [task_definition]
  }

  
  network_configuration {
    subnets          = [var.app_subnet1, var.app_subnet2, var.app_subnet3]
    security_groups  = [aws_security_group.ct_app.id, var.vpn_security_group, var.lb_security_group, aws_security_group.conversion-tool_alb.id]
    assign_public_ip = "false"
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.conversion-tg-ssl.arn
    container_name   = "conversion-tool"
    container_port   = "8443"
  }

  tags = {
    "Name"                = "${var.project_name}-ecs-svc-${var.environment}"
    "qpp:owner"           = var.owner
    "qpp:pagerduty-email" = var.pagerduty_email
    "qpp:application"     = var.application
    "qpp:project"         = var.project_name
    "qpp:environment"     = var.environment
    "qpp:layer"           = "Application"
    "qpp:sensitivity"     = var.sensitivity
    "qpp:description"     = "ECS Service for Conversiontool"
    "qpp:iac-repo-url"    = var.git-origin
  }

}

resource "aws_cloudwatch_log_group" "conversion-tool" {
  name              = "/qppsf/conversion-tool-${var.environment}"
  retention_in_days = 365
}

resource "aws_cloudwatch_log_subscription_filter" "cw-kinesis-subscription-filter" {
  name            = "cwlogs-subscription-to-kinesis"
  role_arn        = aws_iam_role.cwlogs_to_kinesis.arn
  distribution    = "Random"
  log_group_name  = aws_cloudwatch_log_group.conversion-tool.name
  filter_pattern  = ""
  destination_arn = aws_kinesis_stream.kinesis-stream-cw-logs.arn
}

resource "aws_security_group" "ct_app" {
  name        = "conversion-tool-app-${var.environment}"
  description = "Allow inbound traffic"
  vpc_id      = var.vpc_id
  
  ingress {
    from_port       = 8443
    to_port         = 8443
    protocol        = "tcp"
    security_groups = [aws_security_group.conversion-tool_alb.id]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "conversion-tool-app-${var.environment}"
  }
}

resource "aws_security_group" "conversion-tool_alb" {
  name        = "${var.project_name}_${var.environment}_alb_sg"
  description = "Security group for conversion-tool."
  vpc_id      = var.vpc_id
  tags = {
    Name = "${var.project_name}_${var.environment}conversion-tool_alb"
  }
}

resource "aws_security_group_rule" "allow_https" {
  type              = "ingress"
  from_port         = 443
  to_port           = 8443
  protocol          = "tcp"
  cidr_blocks       = var.vpc_cidr
  security_group_id = aws_security_group.conversion-tool_alb.id
}
