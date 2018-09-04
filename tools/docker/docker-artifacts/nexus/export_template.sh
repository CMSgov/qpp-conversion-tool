#!/bin/bash
SCRIPTPATH=$(dirname "$(readlink -f "$0")")
COPY_TO=${1}

apt-get update
apt-get install jq -y

REPO_URL=$(curl -u "${NEXUS_CREDS}" "http://${NEXUS_HOST}:8081/service/rest/beta/repositories" | \
jq -c '[.[] | select(.format=="maven2") | select(.type=="proxy") | .url][0]' -r)

sed -i "s|REPO_URL|${REPO_URL}|g" $SCRIPTPATH/settings.xml
cp $SCRIPTPATH/settings.xml ${COPY_TO}
