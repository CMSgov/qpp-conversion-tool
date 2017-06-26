// Specify the base variables of your environment here.
var env = {
    //organization: 'Example',
    organization: 'Flexion',
    application: 'App',
    environment: 'Test',
    //region: 'us-west-2',
    region: 'us-east-1',
    //bucket: '<YOUR AWS ACCOUNT>-corevpc-deploy-us-west-2',
    bucket: 'qpp-conversion-deployment-corevpc-deploy-us-east-1',
    // Make it match with the GDIT VPC if using GDIT VPC. Otherwise you can
    // make one up, like 10.255.0.0/20.
    //cidr: '10.xx.xx.xx/xx',
    cidr: '10.1.0.0/16',
    creator: 'Flexion',
    keyName: 'qpp-conversion-deployment',
    sslCertificateId: 'arn:aws:acm:us-east-1:684212469706:certificate/6ded07c6-54c2-4678-a0f0-24360905861e',
    defaultIamInstanceProfile: 'server-nonprod', // or use your own
    alarmEmail: 'sfradkin@flexion.us', // for cloudwatch alarms
    machineImageOwners: ['self'],
    externalSecurityGroupRefs: [{ 'Ref': 'OpenHttpSecurityGroup' }]
};

// Boilerplate dependent variables.
env.name = env.organization + env.application + env.environment;
env.s3Url = 's3://' + env.bucket + '/' + env.name.toDash();
env.configPath = env.s3Url + '/config.yaml';
env.fqdn = env.name.toDash() + '.hcgov.internal';

/**
 * Configure GDIT integration if needed, by looking up the IDs of the VPC
 * and subnets and pasting them in here. You can leave this section commented
 * out if you are spinning up this service in a non-GDIT environment -- in
 * that case CoreVPC will create your VPC and subnets for you.
 *
 * Note that 3 subnets for each category is currently required.

 env.vpcId = 'vpc-xxxxxxxx';
 env.gditSubnets = {
    application: ['subnet-xxxxxxx', 'subnet-xxxxxxx', 'subnet-xxxxxxx'],
    management: ['subnet-xxxxxxx', 'subnet-xxxxxxx', 'subnet-xxxxxxx'],
    data: ['subnet-xxxxxxx', 'subnet-xxxxxxx', 'subnet-xxxxxxx'],
    dmz: ['subnet-xxxxxxx', 'subnet-xxxxxxx', 'subnet-xxxxxxx']
};
 */
/**
 * Configure layers needed. You'll add new layers here when you want to add
 * new components or tiers. Comment out one or the other of the following
 * based on whether you're in GDIT or not.
 */
env.configureLayers = function() {

    // GDIT-administered VPC example:
    // return {
    //     app: rootRequire('./layers/app/api'),
    //     jump: rootRequire('./layers/jump/jump'),
    //     net: rootRequire('./layers/net/gdit')
    // };

    // Self-administered VPC example:
    // return {
    //     app: rootRequire('./layers/app/api'),
    //     jump: rootRequire('./layers/jump/jump'),
    //     nat: rootRequire('./layers/nat/nat'),
    //     net: rootRequire('./layers/net/vpc')
    // };

};

/**
 * Run the AMI build script and other checks before cloudforming.
 */
env.preformScript =
    'cd $APP_BASE_DIR; ' +
    '$CORE_BASE_DIR/tools/build-amis.sh service-docker qpp-conversion-tool ' +
    env.region + ' ' +
    '--var-file $APP_BASE_DIR/packer-common.json ' +
    '--var-file $APP_BASE_DIR/packer-app.json';

module.exports = env;
