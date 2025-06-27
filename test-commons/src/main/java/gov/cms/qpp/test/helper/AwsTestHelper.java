package gov.cms.qpp.test.helper;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * A helper class for AWS testing
 */
public class AwsTestHelper {
	public static final String TEST_DYNAMO_TABLE_NAME = "qpp-qrda3converter-acceptance-test";
	public static final String TEST_S3_BUCKET_NAME  = "qpp-qrda3converter-acceptance-test";

	// Shared region constant
	private static final Regions REGION = Regions.US_EAST_1;

	/**
	 * Returns a new DynamoDB client for test operations.
	 * (Eliminates MS_EXPOSE_REP by not exposing internal static fields.)
	 */
	public static AmazonDynamoDB getDynamoClient() {
		return AmazonDynamoDBClientBuilder.standard()
				.withRegion(REGION)
				.build();
	}

	/**
	 * Returns a new S3 client for test operations.
	 */
	public static AmazonS3 getS3Client() {
		return AmazonS3ClientBuilder.standard()
				.withRegion(REGION)
				.build();
	}

	/**
	 * Returns a new KMS client for test operations.
	 */
	public static AWSKMS getKmsClient() {
		return AWSKMSClientBuilder.standard()
				.withRegion(REGION)
				.build();
	}

	/**
	 * Cleans DynamoDB items via batch scan and delete for performance.
	 */
	public static void cleanDynamoDb() {
		AmazonDynamoDB client = getDynamoClient();
		ScanResult scanResult = client.scan(TEST_DYNAMO_TABLE_NAME, Lists.newArrayList("Uuid"));
		List<Map<String, AttributeValue>> metadataList = scanResult.getItems();

		while (scanResult.getLastEvaluatedKey() != null && !scanResult.getLastEvaluatedKey().isEmpty()) {
			scanResult = client.scan(new ScanRequest()
					.withTableName(TEST_DYNAMO_TABLE_NAME)
					.withAttributesToGet("Uuid")
					.withExclusiveStartKey(scanResult.getLastEvaluatedKey()));
			metadataList.addAll(scanResult.getItems());
		}

		metadataList.forEach(map -> client.deleteItem(TEST_DYNAMO_TABLE_NAME, map));
	}

	/**
	 * Cleans up the test S3 bucket by batch for performance purposes.
	 */
	public static void cleanS3() {
		AmazonS3 client = getS3Client();
		ObjectListing objectListing = client.listObjects(TEST_S3_BUCKET_NAME);
		boolean firstTimeThrough = true;

		do {
			if (!firstTimeThrough) {
				objectListing = client.listNextBatchOfObjects(objectListing);
			}

			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				client.deleteObject(TEST_S3_BUCKET_NAME, objectSummary.getKey());
			}

			firstTimeThrough = false;
		} while (objectListing.isTruncated());
	}
}
