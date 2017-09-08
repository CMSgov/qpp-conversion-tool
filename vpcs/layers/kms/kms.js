// var merge = rootRequire('./lib/merge_all');
var shell = rootRequire('shelljs');

function getArn(substitute) {
    substitute = (!substitute) ? 'root' : substitute;
    var userArn = getUserArn();
    return userArn.substring(0, userArn.lastIndexOf(':') + 1) + substitute;
}

function getUserArn() {
    var cmd = 'aws iam get-user';
    var result = shell.exec(cmd, {silent: false});
    console.log('result', result);
    console.log('result code', result.code);
    console.log('result output', result.output);
    if (result.code === 0) {
        var parsed = JSON.parse(result.output);
        console.log('parsed', parsed);
        console.log('arn', parsed['User']['Arn']);
        return parsed['User']['Arn'];
    } else {
        throw new Error('Could not get user arn');
    }
}

function getRoleArn(roleName) {
    var cmd = 'aws iam get-role --role-name ' + roleName;
    var result = shell.exec(cmd, {silent: false});

    if (result.code === 0) {
        return JSON.parse(result.output)['Role']['Arn'];
    } else {
        throw new Error('Could not get role arn for: ' + roleName);
    }
}

function template(keyName, description, enabled, ...roles) {
    var setup = {
        "Resources": {}
    }

    var policy = {
        "Statement": [
            rootStatement(),
            adminStatement(),
            useStatement(roles)
        ]
    };

    setup.Resources[keyName] = {
        "Type" : "AWS::KMS::Key",
        "Properties" : {
            "Description" : description,
            "Enabled" : enabled,
            "KeyPolicy" : policy
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

function rootStatement() {
    return {
        "Sid": "Enable IAM User Permissions",
        "Effect": "Allow",
        "Principal": {
            "AWS": getArn()
        },
        "Action": "kms:*",
        "Resource": "*"
    }
}

function adminStatement() {
    return {
        "Sid": "Allow access for Key Administrators",
        "Effect": "Allow",
        "Principal": {
            "AWS": getUserArn()
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
    }
}

function useStatement(roles) {
    var roleArns = [];

    for (var i in roles) {
        roleArns.push(getArn('role/' + roles[i]));
    }

    return {
        "Sid": "Allow use of the key",
        "Effect": "Allow",
        "Principal": {
            "AWS": roleArns
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
}

module.exports = {
    getUserArn: getUserArn,
    getRoleArn: getRoleArn,
    template: template
}

// {
//     "Statement": [
//     {
//         "Sid": "Enable IAM User Permissions",
//         "Effect": "Allow",
//         "Principal": {
//             "AWS": "arn:aws:iam::452893091467:root"
//         },
//         "Action": "kms:*",
//         "Resource": "*"
//     },
//     {
//         "Sid": "Allow access for Key Administrators",
//         "Effect": "Allow",
//         "Principal": {
//             "AWS": "arn:aws:iam::452893091467:user/converter-deployment"
//         },
//         "Action": [
//             "kms:Create*",
//             "kms:Describe*",
//             "kms:Enable*",
//             "kms:List*",
//             "kms:Put*",
//             "kms:Update*",
//             "kms:Revoke*",
//             "kms:Disable*",
//             "kms:Get*",
//             "kms:Delete*",
//             "kms:TagResource",
//             "kms:UntagResource",
//             "kms:ScheduleKeyDeletion",
//             "kms:CancelKeyDeletion"
//         ],
//         "Resource": "*"
//     },
//     {
//         "Sid": "Allow use of the key",
//         "Effect": "Allow",
//         "Principal": {
//             "AWS": [
//                 "arn:aws:iam::452893091467:role/server-nonprod",
//                 "arn:aws:iam::452893091467:role/server-prod"
//             ]
//         },
//         "Action": [
//             "kms:Encrypt",
//             "kms:Decrypt",
//             "kms:ReEncrypt*",
//             "kms:GenerateDataKey*",
//             "kms:DescribeKey"
//         ],
//         "Resource": "*"
//     },
//     {
//         "Sid": "Allow attachment of persistent resources",
//         "Effect": "Allow",
//         "Principal": {
//             "AWS": [
//                 "arn:aws:iam::452893091467:role/server-nonprod",
//                 "arn:aws:iam::452893091467:role/server-prod"
//             ]
//         },
//         "Action": [
//             "kms:CreateGrant",
//             "kms:ListGrants",
//             "kms:RevokeGrant"
//         ],
//         "Resource": "*",
//         "Condition": {
//             "Bool": {
//                 "kms:GrantIsForAWSResource": "true"
//             }
//         }
//     }
// ]
// }