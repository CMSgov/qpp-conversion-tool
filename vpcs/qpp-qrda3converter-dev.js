var env = {
  name: 'qpp-qrda3converter-dev',
  region: 'us-east-1',
  bucket: 'aws-hhs-cms-ccsq-qpp-navadevops-nonprod-us-east-1',
  s3Url: 's3://aws-hhs-cms-ccsq-qpp-navadevops-nonprod-us-east-1/qpp-qrda3converter-dev',
  keyName: 'nava-sandbox',
  awsAccount: 'aws-hhs-cms-ccsq-qpp-navadevops'
};

/**
 * Configure packer builds.
 */
env.build = {
  region: 'us-east-1',
  playbookName: 'service-docker',
  applicationName: 'Qrda3Converter', // should be the same as env.application
  varFiles: {
    base: ['vpcs/packer-common.json', 'vpcs/packer-base-gdit-july.json'],
    app: ['vpcs/packer-common.json', 'vpcs/packer-app.json']
  }
};

/**
 * Legacy preform script to build AMIs. This script is called by the
 * `cloudform` command before running cloudform. Remove this when we've ported
 * CI over to use the python build-ami script -- at that point, `build-ami`
 * should be run first, then `cloudform` (or terraform) second,
 * as independent steps.
 */
env.preformScript =
  'cd $APP_BASE_DIR; ' +
  '$CORE_BASE_DIR/tools/build-amis.sh service-docker ' +
  env.application + ' ' +
  env.region + ' ' +
  '--var-file $APP_BASE_DIR/vpcs/packer-common.json ' +
  '--var-file $APP_BASE_DIR/vpcs/packer-app.json';

module.exports = env;
