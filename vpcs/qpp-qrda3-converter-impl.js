var env = {
  organization: 'Qpp',
  application: 'Qrda3Converter',
  environment: 'Impl',
  region: 'us-east-1',
  bucket: 'aws-hhs-cms-ccsq-qpp-navadevops-nonprod-us-east-1',
  cidr: '10.138.168.0/21',
  creator: 'Gabe Smedresman',
  keyName: 'nava-sandbox',
  // temporary ssl cert
  sslCertificateId: 'arn:aws:acm:us-east-1:003384571330:certificate/8f110a1b-fc4a-4b94-90b9-f455fc0012c7',
  defaultIamInstanceProfile: 'server-nonprod',
  alarmEmail: 'devops@navahq.com',
  machineImageOwners: ['self'],
  externalSecurityGroupRefs: [{ Ref: 'OpenHttpSecurityGroup' }],
  awsAccount: 'aws-hhs-cms-ccsq-qpp-navadevops'
};

// Boilerplate dependent variables.
env.name = env.organization + env.application + env.environment;
env.s3Url = 's3://' + env.bucket + '/' + env.name.toDash();
env.configPath = env.s3Url + '/config.yaml';
env.fqdn = env.name.toDash() + '.qpp.internal';

env.vpcId = 'vpc-48f26131';
env.gditSubnets = {
  application: ['subnet-bd3f72f5', 'subnet-0f21a355', 'subnet-31957055'],
  management:  ['subnet-c8397480', 'subnet-5c23a106', 'subnet-a78b6ec3'],
  private:     ['subnet-dd3a7795', 'subnet-1e199b44', 'subnet-ed977289'],
  public:      ['subnet-4f3b7607', 'subnet-091c9e53', 'subnet-e98a6f8d']
};

env.configureLayers = function() {
  return {
    app: rootRequire('./layers/app/api'),
    jump: rootRequire('./layers/jump/jump'),
    net: rootRequire('./layers/net/gdit')
  };
};

/**
 * Run the AMI build script and other checks before cloudforming.
 */
env.preformScript =
  'cd $APP_BASE_DIR; ' +
  '$CORE_BASE_DIR/tools/build-amis.sh service-docker ' +
  env.application + ' ' +
  env.region + ' ' +
  '--var-file $APP_BASE_DIR/vpcs/packer-common.json ' +
  '--var-file $APP_BASE_DIR/vpcs/packer-app.json';

module.exports = env;
