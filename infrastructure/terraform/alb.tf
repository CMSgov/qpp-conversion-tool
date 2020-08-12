resource "aws_lb" "qppsf" {
  name               = "qppsf-conversion-tool-lb-${var.environment}"
  internal           = true
  load_balancer_type = "application"
  security_groups    = [var.lb_security_group, aws_security_group.conversion-tool_alb.id]
  subnets            = [var.app_subnet1, var.app_subnet2, var.app_subnet3]

  enable_deletion_protection = true

  tags = {
    Name            = "${var.project_name}-ecr-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_lb_target_group" "conversion-tg" {
  name        = "conversion-tg-${var.environment}"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    protocol = "HTTP"
    path     = "/health"
    matcher  = "200-499"
  }
  tags = {
    Name            = "${var.project_name}-ecr-${var.environment}",
    owner           = var.owner,
    project         = var.project_name
    terraform       = "true"
    pagerduty-email = var.pagerduty_email
    application     = var.application
    sensitivity     = var.sensitivity
    git-origin      = var.git-origin
  }
}

resource "aws_lb_listener" "conversion-tool" {
  load_balancer_arn = aws_lb.qppsf.arn
  port              = "8080"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.conversion-tg.arn
  }
}
# TODO: Access logs

resource "aws_security_group_rule" "ct-ingress-from-http-elb-to-ui" {
  from_port                = 80
  to_port                  = 8080
  protocol                 = "tcp"
  security_group_id        = aws_security_group.ct_app.id
  source_security_group_id = aws_security_group.conversion-tool_alb.id
  type                     = "ingress"
}
