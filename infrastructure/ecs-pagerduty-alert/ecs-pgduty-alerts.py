import json
import re
import logging
logger = logging.getLogger()
import boto3

AWS_REGION = "us-east-1"
CHARSET = "UTF-8"
ses_client = boto3.client('ses',region_name=AWS_REGION)

def lambda_handler(event, context):
    if event["detail-type"] == "ECS Task State Change":
        detail = event["detail"]
        if detail["lastStatus"] == "STOPPED":
            Message=json.dumps(detail)
            messagedata = json.loads(Message)
            Subject='ECS Conversion Tool Notification Alert',
            taskArn = messagedata['taskArn']
            #desiredStatus = messagedata['desiredStatus']
            lastStatus = messagedata['lastStatus']
            stoppedReason = messagedata['stoppedReason']   
            clusterarn = messagedata['clusterArn']
            ecs_region = messagedata['availabilityZone']
            cluster_name = clusterarn.rsplit('/', 1)[-1]
            env = re.findall(r'impl|prod', cluster_name, re.IGNORECASE)[0]
            containerArn = messagedata['containers'][0]['containerArn']
            message_data = "|Alert| ECS Notification for Conversion Tool {}.".format(cluster_name)
            html_message = "Change Detected with ECS Cluster: {}, {} in Region: {}, Environment: {}.".format(cluster_name,clusterarn,ecs_region,env)
            
            
            response = ses_client.send_email(
                Source='893a0342-571a-43d4-ad5e-f4b0aef7654b+CT-routingkey-nonprod@alert.victorops.com',
                Destination={
                    'ToAddresses': ['893a0342-571a-43d4-ad5e-f4b0aef7654b+CT-routingkey-nonprod@alert.victorops.com'],
                    'CcAddresses': ['qpp-final-scoring@cms-qpp.pagerduty.com'],
				    },  
				    Message={
				        'Subject': {
				            'Data': message_data,
				            'Charset' : CHARSET
					},
					'Body': {
						'Text': {
						    'Data':'ECS Notification for Conversion Tool',
							'Charset' : CHARSET
						},
						'Html': {
							'Data': html_message,
							'Charset' : CHARSET
						}
					}
				},  
			)
