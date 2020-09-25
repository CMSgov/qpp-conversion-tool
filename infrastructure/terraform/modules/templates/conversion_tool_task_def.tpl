[
    {
        "environment":[],
        "secrets": [
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/APP_ENV",
                    "name": "APP_ENV"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/AR_API_BASE_URL",
                    "name": "AR_API_BASE_URL"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/BUCKET_NAME",
                    "name": "BUCKET_NAME"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/CPC_END_DATE",
                    "name": "CPC_END_DATE"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/CPC_PLUS_BUCKET_NAME",
                    "name": "CPC_PLUS_BUCKET_NAME"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/CPC_PLUS_UNPROCESSED_FILTER_START_DATE",
                    "name": "CPC_PLUS_UNPROCESSED_FILTER_START_DATE"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/CPC_PLUS_VALIDATION_FILE",
                    "name": "CPC_PLUS_VALIDATION_FILE"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/DB_APP_PASSWORD",
                    "name": "DB_APP_PASSWORD"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/DB_APP_USERNAME",
                    "name": "DB_APP_USERNAME"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/DB_MASTER_PASSWORD",
                    "name": "DB_MASTER_PASSWORD"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/DB_MASTER_USERNAME",
                    "name": "DB_MASTER_USERNAME"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/DEPLOY0A_PUBLIC-I-P",
                    "name": "DEPLOY0A_PUBLIC-I-P"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/DYNAMO_TABLE_NAME",
                    "name": "DYNAMO_TABLE_NAME"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/FMS_TOKEN",
                    "name": "FMS_TOKEN"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/GDIT_NESSUS_PUB_KEY",
                    "name": "GDIT_NESSUS_PUB_KEY"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/JAVA_OPTS",
                    "name": "JAVA_OPTS"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/KMS_KEY",
                    "name": "KMS_KEY"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/NEWRELIC_API_KEY",
                    "name": "NEWRELIC_API_KEY"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/NEXUS_CREDS",
                    "name": "NEXUS_CREDS"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/NEXUS_HOST",
                    "name": "NEXUS_HOST"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/ORG_NAME",
                    "name": "ORG_NAME"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/SPLUNK_TOKEN",
                    "name": "SPLUNK_TOKEN"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/VALIDATION_URL",
                    "name": "VALIDATION_URL"
                },
                {
                    "valueFrom": "arn:aws:ssm:us-east-1:003384571330:parameter/qppar-sf/${env}/conversion_tool/RTI_ORG_NAME",
                    "name": "RTI_ORG_NAME"
                }
        ],
        "command": [
          "/usr/src/run/qppConverter.sh"
        ],
        "essential": true,
        "image": "003384571330.dkr.ecr.us-east-1.amazonaws.com/qppsf/conversion-tool/${env}:latest",
        "logConfiguration": {
            "logDriver": "awslogs",
            "options": {
                "awslogs-group": "/qppsf/conversion-tool-${env}",
                "awslogs-region": "us-east-1",
                "awslogs-stream-prefix": "${env}"
            }
        },
        "mountPoints": [],
        "portMappings": [
      {
        "protocol": "tcp",
        "containerPort": 8080,
        "hostport": 8080
      }
    ],
        "name": "conversion-tool",
        "volumesFrom": []
    }
]
