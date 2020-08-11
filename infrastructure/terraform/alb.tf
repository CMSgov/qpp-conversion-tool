resource "aws_lb" "qppsf" {
  name               = "qppsf-conversion-tool-lb-${var.environment}"
  internal           = true
  load_balancer_type = "application"
  security_groups    = [var.lb_security_group]
  subnets            = [var.app_subnet1, var.app_subnet2, var.app_subnet3]

  enable_deletion_protection = true

  tags = {
    Environment = var.environment
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
}

resource "aws_lb_listener" "conversion-tool" {
  load_balancer_arn = aws_lb.qppsf.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.conversion-tg.arn
  }
}
# TODO: Access logs
