package gov.cms.qpp.test.helper;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * A helper class for aws testing
 */
public class AwsTestHelper {
	public static final String TEST_DYNAMO_TABLE_NAME = "qpp-qrda3converter-acceptance-test";

	private static final AmazonS3 S3_CLIENT = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	private static final AWSKMS KMS_CLIENT = AWSKMSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	private static final String TEST_S3_BUCKET_NAME = "qpp-qrda3converter-acceptance-test";

	public static AmazonS3 getS3Client() {
		return S3_CLIENT;
	}

	public static AWSKMS getKmsClient() {
		return KMS_CLIENT;
	}

	/**
	 * Cleans up the test s3 bucket by batch for performance purposes
	 */
	public static void cleanS3() {
		ObjectListing objectListing = S3_CLIENT.listObjects(TEST_S3_BUCKET_NAME);
		boolean firstTimeThrough = true;

		do {
			if (!firstTimeThrough) {
				objectListing = S3_CLIENT.listNextBatchOfObjects(objectListing);
			}

			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				S3_CLIENT.deleteObject(TEST_S3_BUCKET_NAME, objectSummary.getKey());
			}


			firstTimeThrough = false;
		} while (objectListing.isTruncated());
	}
}
