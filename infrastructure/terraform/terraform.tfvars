#Currently will build in dev if not overwritten
project_name       = "qppsf-ct"
region             = "us-east-1"
vpc_id             = "vpc-0ef66577"
environment        = "dev"
app_subnet1        = "subnet-913b76d9"
app_subnet2        = "subnet-2d23a177"
app_subnet3        = "subnet-d59f7ab1"
vpc_cidr           = ["10.247.224.0/21","10.232.36.0/24"]
app_security_group = "sg-7c84eb08"
vpn_security_group = "sg-15ab1d66"
lb_security_group  = "sg-f996208a"
task_role_arn      = "arn:aws:iam::003384571330:role/converter-nonprod"

