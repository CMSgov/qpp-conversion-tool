def deploy_repo_url= 'git@github.com:CMSgov/qpp-conversion-tool.git'

def cfg = [
  credentials_id: '59771a5a-881e-437b-a2a3-b0f2cb740efa',
  vpc_name: vpc_name,
  // this is the corevpc deploy branch
  // merge into this branch only when you're on a release path
  corevpc_branch: '113e81463a4cce25785aa0e2ffaeb167550642a4',
  aws_account: 'aws-hhs-cms-ccsq-qpp-navadevops',
  terraform_apply: terraform_apply // if true, will apply the terraform plan
]

node('default') {
  git branch: qpp_conversion_branch, credentialsId: cfg.credentials_id, url: deploy_repo_url

  deploy(cfg)
}

// Slack notification using Nava CI slack script
def notify_slack(msg, level = 'INFO') {
  def slack_room = 'mips-deploys'
  sh "/usr/local/bin/jenkins/slack.py --slack_room ${slack_room} --level ${level} \"${msg}\""
}

def deploy(cfg) {
  /*
    Deploys your corevpc service.
    Takes a map (cfg) as a required argument.
    Required keys for the cfg map:
      credentials_id: str, jenkins credential id for github access
      vpc_config_path: str, relative path to the corevpc config file
    Optional cfg map keys: 
      corevpc_branch: str, your deploy branch for corevpc
      aws_account: str, see corevpc:tools/build-amis.sh
      build_ami: bool, whether to build an ami (default true)
  */

  // required cfg params
  assert cfg.credentials_id != null
  assert cfg.vpc_name != null

  def corevpc_repo_url = 'https://github.com/CMSgov/corevpc.git'
  try {
    def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
    def gitCommitLong = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    def msg = "${cfg.vpc_name} begin deploy ${gitCommit} with corevpc#${cfg.corevpc_branch}. Running with terraform_apply=${terraform_apply}."
    notify_slack(msg)
    // clone down corevpc
    cloneRepo(cfg.corevpc_branch, 'git@github.com:CMSgov/corevpc.git', 'corevpc_checkout', cfg.credentials_id)
    sh "./tools/jenkins/corevpc-setup.sh"
    withEnv(['SKIP_MFA=1', 'SKIP_AWS_PROFILE=1', 'AWS_ACCOUNT=' + cfg.aws_account]) {
      sh "./tools/jenkins/build-ami.sh ./vpcs/${cfg.vpc_name}.js"
    }

    // run terraform
    dir("terraform/vpc/environments/${cfg.vpc_name}") {
      sshagent (credentials: [cfg.credentials_id]) {
        // remove old terraform modules if they exist
        // this resolves any issues with different terraform modules loading
        // remove old plan if there is one
        if (fileExists(".terraform/modules")) {
          sh "rm -rf .terraform/modules"
        }

        // clear out old plans
        if (fileExists("plan.out")) {
          sh "rm plan.out"
        }

        // run a terraform init/plan
        sh "terraform init -input=false"
        sh "terraform get -update"

        // make a plan for the web (nginx) deploy
        echo "applying new git hash ${gitCommitLong}"

        echo "Getting plan"
        sh "terraform plan -input=false -var 'app_ami_git_hash=${gitCommitLong}' -out plan.out"
        if (cfg.terraform_apply == "true") {
          echo "Applying instance changes"
          sh "terraform apply -input=false plan.out"
          
          postToNewrelic(gitCommit)
        }
      }
    }   
  } catch (e) {
    def msg = "${cfg.vpc_name} deploy FAILED ${env.BUILD_URL}: ${e}"
    notify_slack(msg, 'ERROR')
    error e
   } finally {
    def msg = "${cfg.vpc_name} end deploy"
    notify_slack(msg)
   }
}

def cloneRepo(branch, url, targetDir, credentials) {
  checkout(
    [$class: 'GitSCM', branches: [[name: branch]],
    doGenerateSubmoduleConfigurations: false,
    extensions: [[$class: 'RelativeTargetDirectory',
    relativeTargetDir: targetDir],
    [$class: 'CleanBeforeCheckout']],
    submoduleCfg: [],
    userRemoteConfigs: [
        [credentialsId: credentials,
        url: url]]]
  )
}

def postToNewrelic(gitCommit) {
  def changeSet = getChangeSets()
  wrap([$class: 'BuildUser']) {
    def user = env.BUILD_USER_ID
    withCredentials([string(credentialsId: 'daff25a9-d1f3-4b39-bf00-d1ebc4068e0e', variable: 'newrelic_rest_key')]) {
      def curlRequest = "curl -sw '%{http_code}' -o /dev/null " \
          + "-X POST 'https://api.newrelic.com/v2/applications/${env.newrelic_app_id}/deployments.json' " \
          + "-H 'X-Api-Key:${newrelic_rest_key}' -i -H 'Content-Type: application/json' " \
          + "-d '{\"deployment\": {\"revision\": \"${gitCommit}\", \"changelog\": \"${changeSet}\", \"description\": \"\", \"user\": \"${user}\"}}'"

      def status = sh(returnStdout: true, script: curlRequest).trim()
      if (status != "201") {
        error("could not post deployment info to newrelic")
      } else {
        echo "posted to newrelic with status ${status}"
      }
    }
  }
}

@NonCPS
def getAllFailedBuilds(failedBuilds, build) {
  if ((build != null) && (build.result != 'SUCCESS')) {
    failedBuilds.add(build)
    getAllFailedBuilds(failedBuilds, build.getPreviousBuild())
  }
}

@NonCPS
def getChangeSets() {
  failedBuilds = []

  getAllFailedBuilds(failedBuilds, currentBuild);

  def text = ""
  for (build in failedBuilds) {
    for (changeSetList in build.changeSets) {
      for (changeSet in changeSetList) {
        // make life easier by stripping special characters
        def strippedMsg = changeSet.msg.replaceAll("[^a-zA-Z0-9 ]+","");
        text += "(${changeSet.commitId}) ${strippedMsg} \\n"
      }
    }
  }

  return text
}
