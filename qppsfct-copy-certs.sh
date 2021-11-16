#!/bin/bash

ENV_CERT=$1
AWS_KEY=$2
AWS_SECRET=$3
AWS_REGION=$4
CERT_CP_PATH="rest-api/src/main/resources/"
CERT="qppsfct-${ENV_CERT}-keystore.p12"
S3BUCKET="qppsf-conversion-tool-artifacts-ssl-bucket/certs"

#Export AWS credentials
export AWS_ACCESS_KEY_ID=${AWS_KEY}
export AWS_SECRET_ACCESS_KEY=${AWS_SECRET}
export AWS_REGION=${AWS_REGION}


cd ./${CERT_CP_PATH}

#Get SSM Secret
export SSL_PASS=$(aws ssm get-parameters --name /qppar-sf/${ENV_CERT}/conversion_tool/SSL_SECRET --with-decryption --query "Parameters[0].Value" | tr -d '"')

if [ ${ENV_CERT} == "devpre" ]; then
    echo "Removing exsisting cert in the current directory"
    rm -f ${CERT}
    echo "Remove last three properties from the config file"
    sed -i "$(( $(wc -l <application.properties)-3+1 )),$ d" application.properties
fi

#Copy certificates from s3 bucket
aws s3 cp s3://${S3BUCKET}/${ENV_CERT}/ . --recursive


APP_PROPERTIES=$(cat <<EOF
server.ssl.key-store=classpath:${CERT}
server.ssl.key-store-password=${SSL_PASS}
server.ssl.key-password=${SSL_PASS}
EOF
)

echo "${APP_PROPERTIES}" >> application.properties


