// EBS FileSystem qpp-<env>-eft
resource "aws_efs_file_system" "qpp-eft" {
    creation_token                  = var.efs_creation_token
    encrypted                       = true
    kms_key_id                      = var.efs_kms_key_id
    tags                            = {
        "Environment"         = var.efs_environment
        "Name"                = var.efs_Name
        "Namespace"           = "qpp"
        "qpp:application"     = var.efs_application
        "qpp:description"     = var.efs_description
        "qpp:environment"     = var.efs_environment
        "qpp:iac-repo-url"    = var.efs_iac-repo-url
        "qpp:owner"           = var.efs_owner
        "qpp:pagerduty-email" = var.efs_pagerduty-email
        "qpp:sensitivity"     = var.efs_sensitivity
    }

    lifecycle_policy {
        transition_to_ia = "AFTER_90_DAYS"
    }
}

// Backup Policy for EBS FileSystem qpp-<env>-eft
resource "aws_efs_backup_policy" "qpp-eft" {
  file_system_id = aws_efs_file_system.qpp-eft.id

  backup_policy {
    status = "ENABLED"
  }
}
