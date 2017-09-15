// Specify the base variables of your environment here.
var env = {
    organization: 'Flexion',
    application: 'qppCnvTl',
    environment: 'Test',
    region: 'us-east-1',
    bucket: 'qpp-conversion-deployment-corevpc-deploy-us-east-1',
    // Make it match with the GDIT VPC if using GDIT VPC. Otherwise you can
    // make one up, like 10.255.0.0/20.
    //cidr: '10.xx.xx.xx/xx',
    cidr: '10.1.0.0/16',
    creator: 'Flexion',
    keyName: 'qpp-conversion-deployment',
    sslCertificateId: 'arn:aws:acm:us-east-1:684212469706:certificate/6ded07c6-54c2-4678-a0f0-24360905861e',
    defaultIamInstanceProfile: 'server-nonprod', // or use your own
    alarmEmail: 'nobody@flexion.us', // for cloudwatch alarms
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
    var nat = rootRequire('./layers/nat/nat');
    var nat1 = nat['Resources']['Nat0aBcdb96d4Instance2'];
    var nat2 = nat['Resources']['Nat0cBcdb96d4Instance2'];
    var nat3 = nat['Resources']['Nat0dBcdb96d4Instance2'];
    nat1['Properties']['ImageId'] = 'ami-d4c5efc2';
    nat1['Properties']['InstanceType'] = 'm4.large';
    nat2['Properties']['ImageId'] = 'ami-d4c5efc2';
    nat2['Properties']['InstanceType'] = 'm4.large';
    nat3['Properties']['ImageId'] = 'ami-d4c5efc2';
    nat3['Properties']['InstanceType'] = 'm4.large';

    var jump = rootRequire('./layers/jump/jump');
    jump['Resources']['JumpInstance']['Properties']['InstanceType'] = 'm4.large';

    var s3 = rootRequire('./vpcs/layers/s3/s3');
    var s3BuckName = env.name + 'audit';
    var rootAccountForAccess = '684212469706';
    var roleUserIdForAccess = 'AROAICQZGCPQQ4EDO764M'; //user ID for the server-nonprod role


    return {
        app: rootRequire('./layers/app/api'),
        s3: s3(s3BuckName, rootAccountForAccess, roleUserIdForAccess),
        jump,
        nat,
        net: rootRequire('./layers/net/vpc')
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
    '--var-file $APP_BASE_DIR/vpcs/flexion/packer-common.json ' +
    '--var-file $APP_BASE_DIR/vpcs/flexion/packer-app.json';

module.exports = env;
