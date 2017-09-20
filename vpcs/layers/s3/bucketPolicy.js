module.exports = function(bucketName, rootAccountNumber, roleUserId) {
	var setup = {
		'Resources': {
		}
	};

	setup.Resources[bucketName + 'Policy'] = {
		'Type': 'AWS::S3::BucketPolicy',
		'Properties': {
			'Bucket': bucketName,
			'PolicyDocument': {
				'Statement': [{
						'Sid': 'DenyUnEncryptedObjectUploads',
						'Effect': 'Deny',
						'Principal': '*',
						'Action': 's3:PutObject',
						'Resource': 'arn:aws:s3:::' + bucketName + '/*',
						'Condition': {
							'StringNotEquals': {
								's3:x-amz-server-side-encryption': 'aws:kms'
							}
						}
					},
					{
						'Sid': 'DenyEveryoneExceptSpecificRole',
						'Effect': 'Deny',
						'Principal': '*',
						'Action': 's3:*',
						'Resource': [
							'arn:aws:s3:::' + bucketName,
							'arn:aws:s3:::' + bucketName + '/*'
						],
						'Condition': {
							'StringNotLike': {
								'aws:userId': [
									roleUserId + ':*',
									rootAccountNumber
								]
							}
						}
					}
				]
			}
		},
		"DeletionPolicy" : "Retain"
	};

	return setup;
}
