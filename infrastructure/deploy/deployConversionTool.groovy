#!/bin/groovy
# Deploys CT tool from ECR repo to ECS
pipeline {

  agent {
    dockerfile { 
      filename 'deploy.Dockerfile'
      dir 'infrastructure/deploy'
    }
  }

environment {
    CLUSTER_NAME='need to create'
    SERVICE_NAME='conversion-tool'
    GIT_HASH_TAG="""${sh(returnStdout: true, script: 'git rev-parse HEAD').trim()}"""
    DEPLOY_TIMEOUT='30'


          stage("Deploy") {
            when {
                anyOf {
                    branch 'develop'; // dev
                    branch 'master'; // prod or impl
                }
            }
            steps {
                sh '''
                    ecs deploy ${CLUSTER_NAME} ${SERVICE_NAME} -t ${GIT_HASH_TAG} \
                    --region us-east-1 --timeout ${DEPLOY_TIMEOUT} --task ${SERVICE_NAME} \
                    --no-deregister
                '''
                sh 'aws ecs wait services-stable --cluster ${CLUSTER_NAME} --services ${SERVICE_NAME} --region us-east-1'
            }
        }
