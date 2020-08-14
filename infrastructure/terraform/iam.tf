#TODO: lockdown resources better
resource "aws_iam_role" "ecs_execution_role" {
  name        = "${var.project_name}-execution-role-${var.environment}"
  description = "Accesss needed to run CT on ECS"

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
      "Sid": "EcsAccess"
    }
  ]
}
EOF

}

resource "aws_iam_policy" "conversion_tool_iam_policy" {
  name = "${var.project_name}-execution-policy-${var.environment}"

  policy = data.template_file.ecs_execution_policy_template.rendered

}

data "template_file" "ecs_execution_policy_template" {
  template = file("${path.module}/templates/task_execution_policy.tpl")

  vars = {
    env = var.environment
  }
}
