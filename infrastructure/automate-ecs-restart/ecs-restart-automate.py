import boto3
import os
client = boto3.client('ecs')
import logging
logger = logging.getLogger()

def lambda_handler(event, context):
    ecs_cluster = os.environ['ecs_cluster']
    ecs_service_name = os.environ['ecs_service']
    client.update_service(cluster=ecs_cluster, service=ecs_service_name, forceNewDeployment=True)
    logger.info("Restarted ECS Service")