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
  internalSecurityGroupRefs: [{ Ref: 'OpenHttpSecurityGroup' }],
  awsAccount: 'aws-hhs-cms-ccsq-qpp-navadevops'
};

// Boilerplate dependent variables.
env.name = env.organization + env.application + env.environment;
env.s3Url = 's3://' + env.bucket + '/' + env.name.toDash();
env.configPath = env.s3Url + '/config.yaml';
env.fqdn = env.name.toDash() + '.qpp.internal';

env.vpcId = 'vpc-0ef66577';
env.gditSubnets = {
  application: ['subnet-913b76d9', 'subnet-2d23a177', 'subnet-d59f7ab1'],
  management:  ['subnet-303c7178', 'subnet-f027a5aa', 'subnet-d49f7ab0'],
  private:     ['subnet-5b3c7113', 'subnet-741d9f2e', 'subnet-43917427'],
  public:      ['subnet-923b76da', 'subnet-9721a3cd', 'subnet-1a94717e']
};

env.configureLayers = function() {
  // Replaces external ELB with an internal ELB
  const virtualEnvironment = rootRequire('./virtual_environment_defs');
  const internalBalancer = rootRequire('./layers/app/balancers/internal_balancer');
  internalBalancer['Resources']['AppElb']['Properties']['Subnets'] = [
    {
      'Ref': virtualEnvironment.zones.private[0].Name
    },
    {
      'Ref': virtualEnvironment.zones.private[1].Name
    },
    {
      'Ref': virtualEnvironment.zones.private[2].Name
    }
  ];
  internalBalancer['Resources']['AppElb']['Properties']['Listeners'][0]['InstancePort'] = 3000;
  internalBalancer['Resources']['AppElb']['Properties']['Listeners'][0]['LoadBalancerPort'] = 443;
  internalBalancer['Resources']['AppElb']['Properties']['Listeners'][0]['Protocol'] = 'HTTPS';
  internalBalancer['Resources']['AppElb']['Properties']['Listeners'][0]['InstanceProtocol'] = 'HTTP';
  // ACM certificate for dev.qpp-qrda3-converter.navapbc.com
  internalBalancer['Resources']['AppElb']['Properties']['Listeners'][0]['SSLCertificateId'] =
    'arn:aws:acm:us-east-1:003384571330:certificate/f1b98858-6b1c-4557-b26b-d2259f5b53e4';

  return {
    app: rootRequire('./layers/app/api'),
    jump: rootRequire('./layers/jump/jump'),
    internalBalancer,
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
