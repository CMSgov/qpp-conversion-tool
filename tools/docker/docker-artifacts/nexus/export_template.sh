#!/bin/bash
SCRIPTPATH=$(dirname "$(readlink -f "$0")")
COPY_TO=${1}
NEXUS_ENV_DIR=${2}

NEXUS_HOST=$(<${NEXUS_ENV_DIR}/NEXUS_HOST)
NEXUS_CREDS=$(<${NEXUS_ENV_DIR}/NEXUS_CREDS)

apt-get update
apt-get install jq -y

echo "nexus env" ${NEXUS_CREDS} ${NEXUS_HOST} > ~/nexus_env_from_template

REPO_URL=$(curl -u "${NEXUS_CREDS}" "http://${NEXUS_HOST}:8081/service/rest/beta/repositories" | \
jq -c '[.[] | select(.format=="maven2") | select(.type=="proxy") | .url][0]' -r)

sed -i "s|REPO_URL|${REPO_URL}|g" $SCRIPTPATH/settings.xml
cp $SCRIPTPATH/settings.xml ${COPY_TO}
