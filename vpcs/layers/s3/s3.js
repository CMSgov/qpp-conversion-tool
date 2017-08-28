var merge = rootRequire('./lib/merge_all');
var virtualEnvironment = rootRequire('./virtual_environment_defs');

module.exports = function(env) {
    var awsAccount = env.awsAccount || "";
    var bucketName = (awsAccount + virtualEnvironment.name + "Bucket").toLowerCase();
    var setup = {
        "Resources": {}
    }

    setup.Resources[bucketName] = {
        "Type" : "AWS::S3::Bucket",
        "Properties" : {
            "AccessControl" : "BucketOwnerFullControl",
            "BucketName" : bucketName,
            "VersioningConfiguration" : {
                "Status" : "Enabled"
            }
        },
        "DeletionPolicy" : "Retain"
    };

    merge([
        setup,
        require('./alarms')(bucketName)
    ]);
}


