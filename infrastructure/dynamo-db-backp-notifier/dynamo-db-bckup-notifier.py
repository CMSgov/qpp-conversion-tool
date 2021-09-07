import boto3
from datetime import datetime, date, timedelta
import logging
import json
import os
import requests

dynamo = boto3.client('dynamodb', region_name='us-east-1')

SLACK_WEBHOOK_URL= os.environ.get[slack_channel_webhook]
SLACK_CHANNEL = os.environ.get[channel_id]

def lambda_handler(event,context):
	response = dynamo.list_backups(
		TableName='qpp-qrda3converter-prod-metadata',
		BackupType='ALL'
	)
	ddb_backup_summaries = response['BackupSummaries']

	if len(ddb_backup_summaries):
		ddb_recent_backups = ddb_backup_summaries[::-1]
		ddb_bkp_creation_datetime_tz = ddb_recent_backups[0]['BackupCreationDateTime']
		ddb_bkp_creation_datetime = ddb_bkp_creation_datetime_tz.replace(tzinfo=None)
		ddb_bkp_name = ddb_recent_backups[0]['BackupName']
		ddb_table_name = ddb_recent_backups[0]['TableName']
		ddb_backup_arn = ddb_recent_backups[0]['BackupArn']
		format_datetime_bkp_creation_datetime = ddb_bkp_creation_datetime_tz.strftime('%b %d %Y, %H:%M:%S %p')
		current_time = datetime.now()
		ddb_backup_time_diff = current_time - ddb_bkp_creation_datetime
		if ddb_backup_time_diff >= timedelta(1):
			payload = {'channel': 'SLACK_CHANNEL', 'text': '%s \n *Table Name*: %s \n *Backup Name*: %s \n *Backup Arn*: %s \n *Last Backup CreatedOn*: %s \n ' % ("*|Alert|* - _Dynamo DB Notification - Recent Backup is older than a day_",ddb_table_name,ddb_bkp_name,ddb_backup_arn, format_datetime_bkp_creation_datetime), 'channel': 'SLACK_CHANNEL' }
			headers = {"content-type": "application/json"}
			requests.put(SLACK_WEBHOOK_URL, data=json.dumps(payload), headers=headers)
		else:
			print("Backup is recent, nothing to do")
	else:
		print("No dynamodb backups have been found")
