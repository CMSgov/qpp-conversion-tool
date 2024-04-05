# Add application variables to SSM, values are created manually, the ones below are placeholders for creating the location in SSM. 
# Re-running this terraform should not overwrite them as "overwrite" defaults to false
# *Uses default KMS key*

# QPPSE-1208
locals {
  commonName        = "${var.project_name}-ssm-${var.environment}"
  commonLayer       = "Application"
  commonSensitivity = "Confidential"
  commonDescription = "SSM Param for Conversiontool"
  ssmappenv_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = "Env for Conversiontool"
    qpp_iac-repo-url    = var.git-origin
  }
  ssmappapibaseurl_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = "AR API URL for Conversiontool"
    qpp_iac-repo-url    = var.git-origin 
  }
  ssmbucket_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = "navadevops Bucket"
    qpp_iac-repo-url    = var.git-origin
  }
  ssmcpcenddate_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = "CPC End Date for Conversiontool"
    qpp_iac-repo-url    = var.git-origin
  }
  ssmcpcplusbkt_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmcpcplus_upfsd_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmcpcplus_vfile_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmdbappuser_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmdbmaster_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmdeploy_publicip_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmdynamo_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmnessuspkey_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmjavaopts_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmctkmskey_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmctnexushost_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmctorgname_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmrtiorgname_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmctvalidurl_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmctsslsecret_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }
  ssmctcertarn_tags = {
    Name                = local.commonName
    qpp_owner           = var.owner
    qpp_incident-response-email = var.pagerduty_email
    qpp_application     = var.application
    qpp_project         = var.project_name
    qpp_environment     = var.environment
    qpp_layer           = local.commonLayer
    qpp_sensitivity     = local.commonSensitivity
    qpp_description     = local.commonDescription
    qpp_iac-repo-url    = var.git-origin
  }

}

resource "aws_ssm_parameter" "app_env" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/APP_ENV"
  description = "APP_ENV"
  type        = "SecureString"
  value       = "dev"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmappenv_tags)

}

resource "aws_ssm_parameter" "ar_api_base_url" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/AR_API_BASE_URL"
  description = "AR_API_BASE_URL"
  type        = "SecureString"
  value       = "https://dev.ar.qpp.internal/api/v1/fms/file/qpp_data/qppct/testCpcPlusValidationFile.json"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmappapibaseurl_tags)
}

resource "aws_ssm_parameter" "bucket_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/BUCKET_NAME"
  description = "BUCKET_NAME"
  type        = "SecureString"
  value       = "aws-hhs-cms-ccsq-qpp-navadevops-pii-convrtr-audt-dev-us-east-1"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmbucket_tags)
}

resource "aws_ssm_parameter" "cpc_end_date" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_END_DATE"
  description = "CPC_END_DATE"
  type        = "SecureString"
  value       = "2021-03-13 - 20:00:00"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmcpcenddate_tags)
}

resource "aws_ssm_parameter" "cpc_plus_bucket_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_PLUS_BUCKET_NAME"
  description = "CPC_PLUS_BUCKET_NAME"
  type        = "SecureString"
  value       = "aws-hhs-cms-ccsq-qpp-navadevops-pii-cnvrt-npicpc-dev-us-east-1"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmcpcplusbkt_tags)
}

resource "aws_ssm_parameter" "cpc_plus_unprocessed_filter_start_date" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_PLUS_UNPROCESSED_FILTER_START_DATE"
  description = "CPC_PLUS_UNPROCESSED_FILTER_START_DATE"
  type        = "SecureString"
  value       = "2020-01-02T04:59:59.999Z"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmcpcplus_upfsd_tags)
}

resource "aws_ssm_parameter" "cpc_plus_validation_file" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CPC_PLUS_VALIDATION_FILE"
  description = "CPC_PLUS_VALIDATION_FILE"
  type        = "SecureString"
  value       = "testCpcPlusValidationFile.json"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmcpcplus_vfile_tags)
}

resource "aws_ssm_parameter" "db_app_username" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DB_APP_USERNAME"
  description = "DB_APP_USERNAME"
  type        = "SecureString"
  value       = "app"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmdbappuser_tags)
}

resource "aws_ssm_parameter" "db_master_username" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DB_MASTER_USERNAME"
  description = "DB_MASTER_USERNAME"
  type        = "SecureString"
  value       = "supersuit"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmdbmaster_tags)
}

resource "aws_ssm_parameter" "deploy0a_public-i-p" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DEPLOY0A_PUBLIC-I-P"
  description = "DEPLOY0A_PUBLIC-I-P"
  type        = "SecureString"
  value       = "34.198.65.93"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmdeploy_publicip_tags)
}

resource "aws_ssm_parameter" "dynamo_table_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/DYNAMO_TABLE_NAME"
  description = "DYNAMO_TABLE_NAME"
  type        = "SecureString"
  value       = "qpp-qrda3converter-dev-metadata"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags, local.ssmdynamo_tags)
}

resource "aws_ssm_parameter" "gdit_nessus_pub_key" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/GDIT_NESSUS_PUB_KEY"
  description = "GDIT_NESSUS_PUB_KEY"
  type        = "SecureString"
  value       = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDLu1Q+kvJSbTD7cKHPwZ9aW3qb0SISHDt3EzEvBTUhGGYYLzG2FFUIkjTd1cc8OyJ9DNlrujIGTNmLjbdQ//F8CtH5990oWN7MtcM0BToTgJ9yhEvhZ7iq2YYxANTBNL6wiTWkS70a6bjsqHGWYJ9jvduGftzHVMYkkapl/oVysRJaNu+38B0Z0FXNmoorlO74/Rt7XK5MhcGbN0z4/1urEWlSl9ygHA4umWw2OM17F6NAEY9fM9W+hcZsK0SzQsFYNI0g1JADKgnXBYOmuPeg/M/6wKctHSiZBporprZ8h7sgK8Sts8Gc/loBVp8DkmnC/eZzi0MnZ4esy6mldW5"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmnessuspkey_tags)
}

resource "aws_ssm_parameter" "java_opts" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/JAVA_OPTS"
  description = "JAVA_OPTS"
  type        = "SecureString"
  ##value       = "-Xms6G -Xmx6G -Xmn5G -XX:+UseStringDeduplication -XX:-AggressiveOpts"
  value       = "-Xms6G -Xmx6G -Xmn5G -XX:+UseStringDeduplication"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmjavaopts_tags)
}

resource "aws_ssm_parameter" "kms_key" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/KMS_KEY"
  description = "KMS_KEY"
  type        = "SecureString"
  value       = "arn:aws:kms:us-east-1:003384571330:alias/qpp-qrda3converter-dev-kms_alias"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmctkmskey_tags)
}

resource "aws_ssm_parameter" "nexus_host" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/NEXUS_HOST"
  description = "NEXUS_HOST"
  type        = "SecureString"
  value       = "ec2-52-201-239-45.compute-1.amazonaws.com"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmctnexushost_tags)
}

resource "aws_ssm_parameter" "org_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/ORG_NAME"
  description = "ORG_NAME"
  type        = "SecureString"
  value       = "cpc-plus-conversion-tool"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmctorgname_tags)
}

resource "aws_ssm_parameter" "rti_org_name" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/RTI_ORG_NAME"
  description = "RTI_ORG_NAME"
  type        = "SecureString"
  value       = "rti-conversion-tool"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmrtiorgname_tags)
}

resource "aws_ssm_parameter" "validation_url" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/VALIDATION_URL"
  description = "VALIDATION_URL"
  type        = "SecureString"
  value       = "https://preview.qpp.cms.gov/api/submissions/public/validate-submission"
  overwrite   = true

  lifecycle {
    ignore_changes = [
      value
    ]
  }

# QPPSE-1208
  tags = merge(var.tags,local.ssmctvalidurl_tags)
}
resource "aws_ssm_parameter" "ssl_secret" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/SSL_SECRET"
  description = "SSL KeyStore Password"
  type        = "SecureString"
  value       = "secret"
  overwrite   = false
  

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  
# QPPSE-1208
  tags = merge(var.tags,local.ssmctsslsecret_tags)
}

resource "aws_ssm_parameter" "cert_arn" {
  name        = "/qppar-sf/${var.environment}/conversion_tool/CERT_ARN"
  description = "SSL Certificate ARN"
  type        = "SecureString"
  value       = "secret"
  overwrite   = false
  

  lifecycle {
    ignore_changes = [
      value
    ]
  }
  
# QPPSE-1208
  tags = merge(var.tags,local.ssmctcertarn_tags)
}