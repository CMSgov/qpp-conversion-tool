def deploy_repo_url= 'https://github.com/CMSgov/qpp-conversion-tool.git'

def cfg = [
  // TODO: is this the right credential?
  credentials_id: '26cbfacb-ed84-45cf-b16b-3bb49681f675',
  vpc_config_path: 'vpcs/${vpc_file}.js',
  // this is the corevpc deploy branch
  // merge into this branch only when you're on a release path
  corevpc_branch: corevpc_branch,
  aws_account: 'aws-hhs-cms-ccsq-qpp-navadevops'
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
  assert cfg.vpc_config_path != null

  def cloudform_flags = ' --yes --disable-rollback';
  if (cfg.build_ami == false) {
    cloudform_flags = cloudform_flags + ' --skip-preform'
  }

  def corevpc_repo_url = 'https://github.com/CMSgov/corevpc.git'
  try {
    def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
    def msg = "${cfg.vpc_config_path} begin deploy ${gitCommit} with corevpc#${cfg.corevpc_branch}"
    notify_slack(msg)
    checkout(
      [$class: 'GitSCM', branches: [[name: cfg.corevpc_branch]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'RelativeTargetDirectory',
      relativeTargetDir: 'corevpc_checkout'],
      [$class: 'CleanBeforeCheckout']],
      submoduleCfg: [],
      userRemoteConfigs: [
          [credentialsId: cfg.credentials_id,
          url: corevpc_repo_url]]]
    )
    sh 'sudo cp corevpc_checkout/cloudform_diff.py /usr/local/bin/cloudform_diff.py'
    withEnv(['COREVPC_SKIP_MFA=1', 'AWS_ACCOUNT=' + cfg.aws_account]) {
        sh "corevpc_checkout/cloudform ${cfg.vpc_config_path} ${cloudform_flags}"
    }
    postToNewrelic(gitCommit)
  } catch (e) {
    def msg = "${cfg.vpc_config_path} deploy FAILED ${env.BUILD_URL}: ${e}"
    notify_slack(msg, 'ERROR')
    error e
   } finally {
    def msg = "${cfg.vpc_config_path} end deploy"
    notify_slack(msg)
   }
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
