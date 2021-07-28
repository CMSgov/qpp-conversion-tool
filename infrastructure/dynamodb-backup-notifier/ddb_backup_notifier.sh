#!/bin/bash

#Script to check for most recent backup, If older than a day, notify team
#Prerequisite - aws cli and jq utilities should be installed

set -x

if ! type jq; then
  printf '%\n' 'Jq not found' >&2
  apk add jq
fi

if ! type aws; then
  printf '%\n' 'awscli not installed' >&2
  exit 1
fi

ddbckup_list="$(aws dynamodb list-backups --table-name qpp-qrda3converter-prod-metadata --backup-type ALL | jq '.[] | length')"

if [ "$ddbckup_list" -ne 0 ]; then
  ddbckp_creation_time="$(aws dynamodb list-backups --table-name qpp-qrda3converter-prod-metadata --backup-type ALL | jq -r '.[]' | jq 'reverse' | jq '.[0].BackupCreationDateTime'|sed -e 's/^"//' -e 's/"$//')" \
  ddbckp_creation_time_ep=$(date -d "$ddbckp_creation_time" +%s) \
  current_time_ep=$(date +%s) \
  time_diff_ep=$((current_time_ep-ddbckp_creation_time_ep)) \
  time_diff_hr=$((time_diff_ep / 3600)) \

  if [ "${time_diff_hr}" -ge "24" ]; then
    echo "Backup is a day or older, Sending a notification "
  else
    echo "Backup is recent, nothing to do "
  fi
else
  echo "No dynamodb backups have been found"
fi
