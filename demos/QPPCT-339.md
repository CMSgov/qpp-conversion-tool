# QPPCT-339

`git checkout 56e047c60b344bfc03cbb7ed739970298e721bde`

# Set-up
- Have a QPP Conversion Tool CoreVPC AMI pre-built. 

# Steps
1. Show JIRA. https://jira.cms.gov/browse/QPPCT-339
1. Change directories to `./terraform/vpc/environments/qpp-qrda3converter-test`.
1. Run `terraform plan --var app_ami_git_hash=e26be883193d2a987d0841e747739fa9293c93be --out plan.out`.
1. Point out the DynamoDB part of the plan.
1. Run `terraform apply plan.out`.
1. Show the created DynamoDB table in AWS.
