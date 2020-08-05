[
    {
        "environment": [
          {
            "name": "APP_ENV",
            "value": "${env}"
          },
          { "name": "NEWRELIC_API_KEY"
            "value": "f9e3fdf92c6f75d1d9378948dfcc68f37ff3c3e5"
          }
        ],
        "essential": true,
        "image": "003384571330.dkr.ecr.us-east-1.amazonaws.com/qppsf/conversion-tool/${env}:latest",
        "logConfiguration": {
            "logDriver": "awslogs",
            "options": {
                "awslogs-group": "/qppsf/conversion-tool",
                "awslogs-region": "us-east-1",
                "awslogs-stream-prefix": "${env}"
            }
        },
        "mountPoints": [],
        "portMappings": [
      {
        "protocol": "tcp",
        "containerPort": 3000,
        "hostPort": 3000
      }
    ],
        "name": "conversion-tool",
        "volumesFrom": []
    }
]
