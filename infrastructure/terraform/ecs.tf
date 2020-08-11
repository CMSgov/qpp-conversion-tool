resource "aws_ecs_cluster" "conversion-tool-ecs-cluster" {
  name = "qppsf-conversion-tool-${var.environment}"
}

resource "aws_ecs_task_definition" "conversion-tool" {
  family                   = "qppsf-conversion-tool"
  execution_role_arn       = "arn:aws:iam::003384571330:role/ecsTaskExecutionRole"
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

  network_configuration {
    subnets          = [var.app_subnet1, var.app_subnet2, var.app_subnet3]
    security_groups  = [aws_security_group.ct_app.id, var.vpn_security_group]
    assign_public_ip = "false"
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.conversion-tg.arn
    container_name   = "conversion-tool"
    container_port   = "8080"
  }

}

resource "aws_cloudwatch_log_group" "conversion-tool" {
  name              = "/qppsf/conversion-tool"
  retention_in_days = 30
}

resource "aws_security_group" "ct_app" {
  name        = "conversion_tool-app${var.environment}"
  description = "Allow TLS inbound traffic"
  vpc_id      = var.vpc_id

  ingress {
    description = "TLS from VPC"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = var.vpc_cidr
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
