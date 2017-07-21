var env = {
  organization: 'Qpp',
  application: 'Qrda3Converter',
  environment: 'Dev',
  region: 'us-east-1',
  bucket: 'aws-hhs-cms-ccsq-qpp-navadevops-nonprod-us-east-1',
  cidr: '10.247.224.0/21',
  creator: 'Gabe Smedresman',
  keyName: 'nava-sandbox',
  // temporary ssl cert
  sslCertificateId: 'arn:aws:acm:us-east-1:003384571330:certificate/8f110a1b-fc4a-4b94-90b9-f455fc0012c7',
  defaultIamInstanceProfile: 'server-nonprod',
  alarmEmail: 'devops@navahq.com',
  machineImageOwners: ['self'],
  externalSecurityGroupRefs: [{ Ref: 'OpenHttpSecurityGroup' }]
};

// Boilerplate dependent variables.
env.name = env.organization + env.application + env.environment;
env.s3Url = 's3://' + env.bucket + '/' + env.name.toDash();
env.configPath = env.s3Url + '/config.yaml';
env.fqdn = env.name.toDash() + '.qpp.internal';

env.vpcId = 'vpc-0ef66577';
env.gditSubnets = {
  application: ['subnet-913b76d9', 'subnet-2d23a177', 'subnet-d59f7ab1'],
  management: ['subnet-303c7178', 'subnet-f027a5aa', 'subnet-d49f7ab0'],
  data: ['subnet-5b3c7113', 'subnet-741d9f2e', 'subnet-43917427'],
  dmz: ['subnet-923b76da', 'subnet-9721a3cd', 'subnet-1a94717e']
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
  '--var-file $APP_BASE_DIR/corevpc/packer-common.json ' +
  '--var-file $APP_BASE_DIR/corevpc/packer-app.json';

module.exports = env;
