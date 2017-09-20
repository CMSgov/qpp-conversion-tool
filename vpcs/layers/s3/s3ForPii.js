var merge = rootRequire('./lib/merge_all');

module.exports = function(bucketName, rootAccountNumberForAccess, roleUserIdForAccess) {
    var setup = {
        "Resources": {}
    }

    setup.Resources[bucketName] = {
        "Type" : "AWS::S3::Bucket",
        "Properties" : {
            "AccessControl" : "BucketOwnerFullControl",
            "BucketName" : bucketName
        },
        "DeletionPolicy" : "Retain"
    };

    return merge([
        setup,
        require('./alarms')(bucketName),
        require('./bucketPolicy')(bucketName, rootAccountNumberForAccess, roleUserIdForAccess)
    ]);
}
