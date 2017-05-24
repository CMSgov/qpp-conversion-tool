#!/bin/bash
name=$1
baseDir=$(dirname $0)
source ${baseDir}/envVars.sh

docker-machine create \
--driver amazonec2 \
--amazonec2-access-key ${ACCESS_KEY} \
--amazonec2-secret-key ${SECRET_KEY} \
--amazonec2-vpc-id ${VPC_ID} \
--amazonec2-subnet-id ${SUBNET_ID} \
--amazonec2-region us-east-1 ${name}

localKey=$(cat ~/.ssh/id_rsa.pub) && \
docker-machine ssh ${name} "echo "$localKey" >> .ssh/authorized_keys"
docker-machine scp -r ${baseDir}/instanceInit ${name}:/home/ubuntu/
docker-machine ssh ${name} "chmod +x /home/ubuntu/instanceInit/init.sh && /home/ubuntu/instanceInit/init.sh"