
function template(keyName, roles, options) {
    var setup = {
        "Resources": {}
    };

    setup.Resources[keyName] = {
        "Type" : "AWS::KMS::Key",
        "Properties" : {
            "Description" : options.description || '',
            "Enabled" : !!options.enabled,
            "KeyPolicy" : policy(roles)
        }
    };

    setup.Resources[keyName + 'Alias'] = {
        "Type" : "AWS::KMS::Alias",
        "Properties" : {
            "AliasName" : "alias/" + keyName,
            "TargetKeyId" : {
                "Ref": keyName
            }
        }
    };

    return setup;
}

function policy(roles) {
    return {
        "Statement": [
            {
                "Sid": "Enable IAM User Permissions",
                "Effect": "Allow",
                "Principal": {
                    "AWS": roles.supers
                },
                "Action": "kms:*",
                "Resource": "*"
            },{
                "Sid": "Allow access for Key Administrators",
                "Effect": "Allow",
                "Principal": {
                    "AWS": roles.administrators
                },
                "Action": [
                    "kms:Create*",
                    "kms:Describe*",
                    "kms:Enable*",
                    "kms:List*",
                    "kms:Put*",
                    "kms:Update*",
                    "kms:Revoke*",
                    "kms:Disable*",
                    "kms:Get*",
                    "kms:Delete*",
                    "kms:TagResource",
                    "kms:UntagResource",
                    "kms:ScheduleKeyDeletion",
                    "kms:CancelKeyDeletion"
                ],
                "Resource": "*"
            },{
                "Sid": "Allow use of the key",
                "Effect": "Allow",
                "Principal": {
                    "AWS": roles.users
                },
                "Action": [
                    "kms:Encrypt",
                    "kms:Decrypt",
                    "kms:ReEncrypt*",
                    "kms:GenerateDataKey*",
                    "kms:DescribeKey"
                ],
                "Resource": "*"
            }
        ]
    }
}

module.exports = template;
