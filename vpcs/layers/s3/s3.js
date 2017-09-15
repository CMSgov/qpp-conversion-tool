var merge = rootRequire('./lib/merge_all');

module.exports = function(bucketName) {
    var setup = {
        "Resources": {}
    }

    var bucketNameLowerCase = bucketName.toLowerCase();

    setup.Resources[bucketNameLowerCase] = {
        "Type" : "AWS::S3::Bucket",
        "Properties" : {
            "AccessControl" : "BucketOwnerFullControl",
            "BucketName" : bucketNameLowerCase
        },
        "DeletionPolicy" : "Retain"
    };

    return merge([
        setup,
        require('./alarms')(bucketNameLowerCase),
        require('./encryptionPolicy')(bucketNameLowerCase)
    ]);
}
