{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ECSTaskAccess",
            "Effect": "Allow",
            "Action": [
                "s3:GetAccessPoint",
                "ssm:DescribeDocument",
                "kms:GenerateRandom",
                "ec2messages:GetEndpoint",
                "ssmmessages:OpenControlChannel",
                "ec2messages:GetMessages",
                "ssm:PutConfigurePackageResult",
                "ssm:ListInstanceAssociations",
                "ssm:GetParameter",
                "ssm:UpdateAssociationStatus",
                "ssm:GetManifest",
                "kms:DescribeCustomKeyStores",
                "kms:DeleteCustomKeyStore",
                "ec2messages:DeleteMessage",
                "ssm:UpdateInstanceInformation",
                "kms:UpdateCustomKeyStore",
                "ec2messages:FailMessage",
                "ssmmessages:OpenDataChannel",
                "ssm:GetDocument",
                "kms:CreateKey",
                "kms:ConnectCustomKeyStore",
                "s3:HeadBucket",
                "ssm:PutComplianceItems",
                "ssm:DescribeAssociation",
                "s3:PutAccountPublicAccessBlock",
                "ssm:GetDeployablePatchSnapshotForInstance",
                "s3:ListAccessPoints",
                "s3:ListJobs",
                "ec2messages:AcknowledgeMessage",
                "ssm:GetParameters",
                "ssmmessages:CreateControlChannel",
                "kms:CreateCustomKeyStore",
                "ssmmessages:CreateDataChannel",
                "kms:ListKeys",
                "ssm:PutInventory",
                "s3:GetAccountPublicAccessBlock",
                "s3:ListAllMyBuckets",
                "kms:ListAliases",
                "kms:DisconnectCustomKeyStore",
                "ec2messages:SendReply",
                "s3:CreateJob",
                "ssm:ListAssociations",
                "ssm:UpdateInstanceAssociationStatus"
            ],
            "Resource": "*"
        },
        {
            "Sid": "KmsAccess",
            "Effect": "Allow",
            "Action": [
              "kms:Decrypt",
              "kms:DescribeKey",
              "kms:Encrypt",
              "kms:GenerateDataKey*",
              "kms:ReEncrypt*"
        ],
            "Resource": "arn:aws:kms:*:*:key/*"
        },
        {
            "Sid": "S3Access",
            "Effect": "Allow",
            "Action": "s3:*",
            "Resource": [
                "arn:aws:s3:::*",
                "arn:aws:s3:::*/*"
            ]
        }
    ]
}
