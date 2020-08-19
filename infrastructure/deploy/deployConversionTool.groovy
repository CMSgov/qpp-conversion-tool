#!/bin/groovy
// Deploys CT tool from ECR repo to ECS
pipeline {

  agent {
    dockerfile { 
      filename 'deploy.Dockerfile'
      dir 'infrastructure/deploy'
    }
  }

  environment {
    CLUSTER_NAME='qppsf-conversion-tool'
    SERVICE_NAME='conversion-tool'
    GIT_HASH_TAG="""${sh(returnStdout: true, script: 'git rev-parse HEAD').trim()}"""
    DEPLOY_TIMEOUT='600'
  }
  
  parameters {
    choice(name: 'env', choices: ['dev', 'devpre', 'impl', 'prod'], description: 'Which environment do you want to run it in?')
  }

  stages {

    stage("Deploy") {
      steps {
        sh 'env'
        sh '''
         ecs deploy ${CLUSTER_NAME}-${env} ${SERVICE_NAME}-service-${env} -t ${GIT_HASH_TAG} \
          --region us-east-1 --timeout ${DEPLOY_TIMEOUT} \
          --no-deregister
          '''
        sh 'aws ecs wait services-stable --cluster ${CLUSTER_NAME}-${env} --services ${SERVICE_NAME}-${env} --region us-east-1'
      }
    }
  }

  post {
//    failure {
//      script {
//          slackSend channel: '@jeremy.page', slackSend color: 'Error', message: '${currentBuild.fullDisplayName} failed'
//        }
//      }
    always {
       deleteDir()
    }
  }
}
