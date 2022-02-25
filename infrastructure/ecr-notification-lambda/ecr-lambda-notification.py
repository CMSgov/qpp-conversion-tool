import boto3
import json
import logging
import os
import time

from base64 import b64decode
from urllib.request import Request, urlopen
from urllib.error import URLError, HTTPError

logger = logging.getLogger()

def lambda_handler(event, context):

    message = event['detail']
    result  = event['detail']['scan-status']
    findings = event['detail']['finding-severity-counts']
    repo    = event['detail']['repository-name']

    response = f"An Image was pushed to ECR {repo} with {findings}"
    slack_color = "GREEN"

    slack_message = {
        'channel': os.environ['channel'],
        'attachments': [
            {
                'color': slack_color,
                'text': response,
                'ts' : int(time.time())
            }
        ]
    }
    req = Request(os.environ['hook_url'], json.dumps(slack_message).encode('utf-8'))
    try:
        response = urlopen(req)
        response.read()
        logger.info("Message posted to %s", slack_message['channel'])
    except HTTPError as e:
        logger.error("Request failed: %d %s", e.code, e.reason)
    except URLError as e:
        logger.error("Server connection failed: %s", e.reason)