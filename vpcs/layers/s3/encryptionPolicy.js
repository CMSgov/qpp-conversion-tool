module.exports = function(bucketName) {
	var setup = {
		'Resources': {
		}
	};

	setup.Resources[bucketName + 'EncryptionPolicy'] = {
		'Type': 'AWS::S3::BucketPolicy',
		'Properties': {
			'Bucket': bucketName,
			'PolicyDocument': {
				'Statement': [{
						'Sid': 'DenyUnEncryptedObjectUploads',
						'Effect':'Deny',
						'Principal': '*',
						'Action': 's3:PutObject',
						'Resource': 'arn:aws:s3:::' + bucketName + '/*',
						'Condition': {
							'StringNotEquals': {
								's3:x-amz-server-side-encryption': 'aws:kms'
							}
						}
					}
				]
			}
		}
	};

	return setup;
}
