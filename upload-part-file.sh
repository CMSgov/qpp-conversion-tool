#!/bin/bash

#${{ format('aws s3 cp s3://{0}/{1} .', secrets.DEV_S3_BUCKET, secrets.DEV_PART_FILE) }}

PART_FILE_BUCKET=$1
PART_FILE=$2
FORMATTED_FILE_NAME=$3
AWS_REGION=$4

export AWS_REGION=${AWS_REGION}

aws s3 cp s3://${PART_FILE_BUCKET}/${PART_FILE} .
chmod +x ./tools/scripts/format-participation-file.py
python3 ./tools/scripts/format-participation-file.py ${PART_FILE} ${FORMATTED_FILE_NAME}
aws s3 mv ${FORMATTED_FILE_NAME} s3://${PART_FILE_BUCKET}/${FORMATTED_FILE_NAME}

rm ${FORMATTED_FILE_NAME}