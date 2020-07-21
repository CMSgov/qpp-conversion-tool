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
    DEPLOY_TIMEOUT='30'
  }
  
  parameters {
    choice(name: 'env', choices: ['prod', 'impl', 'dev'], description: 'Which environment do you want to run it in?')
  }

  stages {

    stage("Deploy") {
  //    when {
  //      anyOf {
  //        branch 'develop'; // dev
  //        branch 'master'; // prod or impl
  //        branch 'QPPSF-6388_Migrate_CT_Deployment' ; //feature branch
  //      }
   //   }
      
      steps {
        sh '''
         ecs deploy ${CLUSTER_NAME}-${params.enviroment} ${SERVICE_NAME}-${params.enviroment} -t ${GIT_HASH_TAG} \
          --region us-east-1 --timeout ${DEPLOY_TIMEOUT} --task ${SERVICE_NAME}-${params.enviroment} \
          --no-deregister
          '''
        sh 'aws ecs wait services-stable --cluster ${CLUSTER_NAME}-${params.enviroment} --services ${SERVICE_NAME}-${params.enviroment} --region us-east-1'
      }
    }
  }

//  post {
//    failure {
//      script {
//          slackSend channel: '@jeremy.page', slackSend color: 'Error', message: '${currentBuild.fullDisplayName} failed'
//        }
//      }
//    always {
//       deleteDir()
//    }
//  }
}
