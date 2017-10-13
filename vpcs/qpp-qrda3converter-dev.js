var env = {
  name: 'qpp-qrda3converter-dev',
  region: 'us-east-1',
  bucket: 'aws-hhs-cms-ccsq-qpp-navadevops-nonprod-us-east-1',
  // Used by vpc-env to store environmnet variables
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

module.exports = env;
