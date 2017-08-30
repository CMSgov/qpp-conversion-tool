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
  internalSecurityGroupRefs: [{ Ref: 'OpenHttpSecurityGroup' }],
  awsAccount: 'aws-hhs-cms-ccsq-qpp-navadevops'
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

  Object.assign(internalBalancer['Resources']['AppElb']['Properties']['Listeners'][0], {
    InstancePort: 3000,
    LoadBalancerPort: 443,
    Protocol: 'HTTPS',
    InstanceProtocol: 'HTTP',
    // ACM certificate for prod.qpp-qrda3-converter.navapbc.com
    SSLCertificateId: 'arn:aws:acm:us-east-1:003384571330:certificate/a54f7f91-5268-4321-b2ba-f282e65a5c4e'
  });

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
