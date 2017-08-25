var merge = rootRequire('./lib/merge_all');
var virtualEnvironment = rootRequire('./virtual_environment_defs');
var bucketName = (virtualEnvironment.name + "Bucket").toLowerCase();

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

module.exports = merge([
    setup,
    require('./alarms')(bucketName)
]);

