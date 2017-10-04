Install [Terraform](terraform.io)

```shell
build-ami qpp-qrda3converter-dev

# this is the git hash of the release you are deploying, created with `build-ami`
export DEPLOYED_GIT_HASH=<hash of the AMI you are deploying>

cd terraform/vpc/environments/qpp-qrda3converter-dev

# Initialize the Terraform state and fetch modules
terraform init

# Dry run with terraform plan
terraform plan -var "app_ami_git_hash=$DEPLOYED_GIT_HASH"

# If all looks good, run terraform apply
terraform apply -var "app_ami_git_hash=$DEPLOYED_GIT_HASH"
```
