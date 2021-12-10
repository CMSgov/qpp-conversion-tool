resource "aws_lb" "qppsf" {
  name               = "qppsf-conversion-tool-lb-${var.environment}"
  internal           = true
  load_balancer_type = "application"
  security_groups    = [aws_security_group.ct_app.id, var.vpn_security_group, var.lb_security_group, aws_security_group.conversion-tool_alb.id]
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
  access_logs {
    bucket  = aws_s3_bucket.log_bucket.id
    prefix  = "conversion-tool/${var.environment}"
    enabled = true
  }
}

#ALB Target group for HTTPS
resource "aws_lb_target_group" "conversion-tg-ssl" {
  name        = "conversion-tg-${var.environment}-ssl"
  port        = 8443
  protocol    = "HTTPS"
  vpc_id      = var.vpc_id
  target_type = "ip"

  depends_on = [aws_lb.qppsf]

  health_check {
    protocol = "HTTPS"
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

#ALB Listener for HTTPS
resource "aws_lb_listener" "conversion-tool-ssl" {
  load_balancer_arn = aws_lb.qppsf.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = var.certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.conversion-tg-ssl.arn
  }
}

resource "aws_security_group_rule" "ct-ingress-from-https-elb-to-ui" {
  from_port                = 443
  to_port                  = 8443
  protocol                 = "tcp"
  security_group_id        = aws_security_group.ct_app.id
  source_security_group_id = aws_security_group.conversion-tool_alb.id
  type                     = "ingress"
}
