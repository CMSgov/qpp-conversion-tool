#!/bin/bash

PART_FILE_BUCKET=$1
PART_FILE=$2
FORMATTED_FILE_NAME=$3
AWS_REGION=$4

export AWS_REGION=${AWS_REGION}

pip install openpyxl
pip install simplejson

aws s3 --region=${AWS_REGION} cp s3://${PART_FILE_BUCKET}/${PART_FILE} .
#chmod +x ./tools/scripts/retrieve-fms-file.py
# chmod +x ./tools/scripts/format-participation-file.py
# #python ./tools/scripts/retrieve-fms-file.py -au ${AUTH_URL} -fu ${FMS_URL} -t ${FMS_TOKEN} -p ${FMS_PATH}
# python ./tools/scripts/format-participation-file.py ${PART_FILE} ${FORMATTED_FILE_NAME}
# aws s3 mv ${FORMATTED_FILE_NAME} s3://${PART_FILE_BUCKET}/${FORMATTED_FILE_NAME}

if test -f "$FORMATTED_FILE_NAME"
then
  echo 'Removing Formatted Participation file localy...'
  rm ${FORMATTED_FILE_NAME}
  echo 'Formatted Participation file has been removed locally.'
else
  echo 'Formatted Participation file has been removed locally.'
fi