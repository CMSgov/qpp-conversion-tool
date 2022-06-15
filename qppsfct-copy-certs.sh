#!/bin/bash

ENV_CERT=$1
AWS_KEY=$2
AWS_SECRET=$3
AWS_REGION=$4
CERT_CP_PATH="rest-api/src/main/resources/"

#Export AWS credentials
export AWS_ACCESS_KEY_ID=${AWS_KEY}
export AWS_SECRET_ACCESS_KEY=${AWS_SECRET}
export AWS_REGION=${AWS_REGION}
export ENV_CERT=${ENV_CERT}

#Export Passphrase for Environment
export SSL_PASS=$(aws ssm get-parameters --name /qppar-sf/${ENV_CERT}/conversion_tool/SSL_SECRET --with-decryption --query "Parameters[0].Value" | tr -d '"')

#Export Certificate ARN for Environment
export CERT_ARN=$(aws ssm get-parameters --name /qppar-sf/${ENV_CERT}/conversion_tool/CERT_ARN --with-decryption --query "Parameters[0].Value" | tr -d '"')

cd ./${CERT_CP_PATH}

if [ ${ENV_CERT} == "devpre" ]; then
    echo "Removing any certs in the current directory"
    rm -f ./${ENV_CERT}_cert.p12
    echo "Remove last three properties from the config file"
    sed -i "$(( $(wc -l <application.properties)-3+1 )),$ d" application.properties
fi

#Encoded passphrase in bas64
echo -n ${SSL_PASS}|base64 > ./passphrase.txt

#Export Certificate Chain from ACM
aws acm export-certificate --certificate-arn ${CERT_ARN} --passphrase file://passphrase.txt --output text > ./fullcert.pem

awk '{$1=$1};1'<./fullcert.pem | tee ./certificate.pem > /dev/null

#Separate Private Key from fullchain certificate
awk '/-----BEGIN ENCRYPTED PRIVATE KEY-----/ {f=1} /-----END ENCRYPTED PRIVATE KEY-----/ {print; f=0} f' ./certificate.pem > ./privatekey.pem

awk '{$1=$1};1'<./privatekey.pem | tee ./encryptedkey.pem > /dev/null

#Decrypt Private Key
openssl rsa -in ./encryptedkey.pem -out ./decryptedkey.pem -passin pass:${SSL_PASS}

#Export PEM to P12 Format
openssl pkcs12 -export -out ./${ENV_CERT}_cert.p12 -in ./certificate.pem -inkey ./decryptedkey.pem -password pass:${SSL_PASS}

printf "%s\n" "server.ssl.key-store=classpath:${ENV_CERT}_cert.p12" "server.ssl.key-store-password=${SSL_PASS}" "server.ssl.key-password=${SSL_PASS}" >> application.properties

#Clean-up certificate files
rm -f ./passphrase.txt
rm -f *.pem