#Currently will build in dev if not overwritten
project_name         = "qppsf-ct"
team                 = "qppsf"
region               = "us-east-1"
vpc_id               = "vpc-0ef66577"
environment          = "dev"
app_subnet1          = "subnet-913b76d9"
app_subnet2          = "subnet-2d23a177"
app_subnet3          = "subnet-d59f7ab1"
vpc_cidr             = ["10.247.224.0/21", "10.232.36.0/24"]
vpn_security_group   = "sg-15ab1d66"
lb_security_group    = "sg-01f29f7f32a8789ef"
pagerduty_email      = "893a0342-571a-43d4-ad5e-f4b0aef7654b+CT-routingkey-nonprod@alert.victorops.com"
owner                = "qpp-final-scoring-devops@semanticbits.com"
git-origin           = "https://github.com/CMSgov/qpp-conversion-tool.git"
application          = "qpp-conversiontool"
sensitivity          = "PII/PHI"
certificate_arn      = "arn:aws:acm:us-east-1:003384571330:certificate/4e09608a-6e5f-4a30-a2aa-e0b4e257eeef"
codebuild_branch_ref = "refs/heads/develop"
allow_kms_keys       = ["arn:aws:kms:us-east-1:003384571330:key/ff7fca93-2b54-402a-9734-73d5b8538943","arn:aws:kms:us-east-1:003384571330:key/ecc43cde-608b-488b-8295-41a8d9bee42d","arn:aws:kms:us-east-1:003384571330:key/eb29db32-6833-4a3c-b067-a55fcf0c48c6","arn:aws:kms:us-east-1:003384571330:key/1863d256-47dd-4875-ac19-b744853f3609","arn:aws:kms:us-east-1:003384571330:key/320a1f44-7ffd-4ede-817b-a738375514b6","arn:aws:kms:us-east-1:003384571330:key/3f32fbb4-f735-48fd-bb02-1804f3d8f45a","arn:aws:kms:us-east-1:003384571330:key/4d9c8e8f-eee7-410a-a325-7c6a79bdbc31","arn:aws:kms:us-east-1:003384571330:key/71ca03b3-d3f2-4b6b-a250-59d5af6804c6","arn:aws:kms:us-east-1:003384571330:key/79839ee5-a18c-40c3-9efa-71c488ad4589","arn:aws:kms:us-east-1:003384571330:key/7b57229b-8dfc-4121-8cb3-939acf91ac09","arn:aws:kms:us-east-1:003384571330:key/895c193a-b42d-4a66-96cd-136f2da62133","arn:aws:kms:us-east-1:003384571330:key/a4507982-fe74-4f96-845a-c7552dbf99cb","arn:aws:kms:us-east-1:003384571330:key/bc788e72-24df-447d-852c-47f1bb14e4a9","arn:aws:kms:us-east-1:003384571330:key/e8593690-f5bf-44a6-a30b-2cee79307a25"]
conversion_tool_service_desired_count = 0

# create above allow_kms_keys:
# $ aws kms list-keys --no-paginate --query 'Keys[].KeyArn' 