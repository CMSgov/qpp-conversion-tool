var env = {
  organization: 'Qpp',
  application: 'Qrda3Converter',
  environment: 'Prod',
  region: 'us-east-1',
  bucket: 'aws-hhs-cms-ccsq-qpp-navadevops-prod-us-east-1',
  cidr: '10.245.200.0/21',
  creator: 'Gabe Smedresman',
  keyName: 'nava-sandbox',
  // temporary ssl cert
  sslCertificateId: 'arn:aws:acm:us-east-1:003384571330:certificate/8f110a1b-fc4a-4b94-90b9-f455fc0012c7',
  defaultIamInstanceProfile: 'server-prod',
  alarmEmail: 'devops@navahq.com',
  machineImageOwners: ['self'],
  externalSecurityGroupRefs: [{ Ref: 'OpenHttpSecurityGroup' }]
};

// Boilerplate dependent variables.
env.name = env.organization + env.application + env.environment;
env.s3Url = 's3://' + env.bucket + '/' + env.name.toDash();
env.configPath = env.s3Url + '/config.yaml';
env.fqdn = env.name.toDash() + '.qpp.internal';

env.vpcId = 'vpc-fa45d483';
env.gditSubnets = {
  application: ['subnet-3df1b275', 'subnet-624ecd38', 'subnet-383e245d'],
  management:  ['subnet-59f1b211', 'subnet-8244c7d8', 'subnet-d52a30b0'],
  private:     ['subnet-aaeba8e2', 'subnet-2b44c771', 'subnet-25372d40'],
  public:      ['subnet-b4e8abfc', 'subnet-f245c6a8', 'subnet-32342e57']
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
