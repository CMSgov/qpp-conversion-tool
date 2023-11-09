This contains the scripts and terraform to create the CT tool application's infastructure.

Terraform for:
 * ELB
 * ECR Repository
 * Fargate TD
 * Fargate Service

There is a terraform.tfvars file that defaults to the dev environment. For other environments you will need to provide an override.
  
Standardized resource tagging has been implemented with support by AWS provider v3.38.0+ as documented https://developer.hashicorp.com/terraform/tutorials/aws/aws-default-tags?in=terraform%2Faws.
 * see dev/main.tf and dev/variables.tf for baseline pattern
 * see modules/kinesis.tf for implementation of merging defaults with resource-specific tags
