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
 * A helper class for aws testing
 */
public class AwsTestHelper {
	public static final String TEST_DYNAMO_TABLE_NAME = "qpp-qrda3converter-acceptance-test";

	private static AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	private static AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	private static AWSKMS kmsClient = AWSKMSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	private static final String TEST_S3_BUCKET_NAME = "qpp-qrda3converter-acceptance-test";

	public static AmazonDynamoDB getDynamoClient() {
		return dynamoClient;
	}

	public static AmazonS3 getS3Client() {
		return s3Client;
	}

	public static AWSKMS getKmsClient() {
		return kmsClient;
	}

	/**
	 * Cleans dynamodb items until via batch scan and delete for performance purposes
	 */
	public static void cleanDynamoDb() {
		ScanResult scanResult = dynamoClient.scan(TEST_DYNAMO_TABLE_NAME, Lists.newArrayList("Uuid"));
		List<Map<String, AttributeValue>> metadataList = scanResult.getItems();
		while (scanResult.getLastEvaluatedKey() != null && !scanResult.getLastEvaluatedKey().isEmpty()) {
			scanResult = dynamoClient.scan(new ScanRequest().withTableName(TEST_DYNAMO_TABLE_NAME).withAttributesToGet("Uuid")
				.withExclusiveStartKey(scanResult.getLastEvaluatedKey()));
			metadataList.addAll(scanResult.getItems());
		}

		metadataList.forEach(map -> dynamoClient.deleteItem(TEST_DYNAMO_TABLE_NAME, map));
	}

	/**
	 * Cleans up the test s3 bucket by batch for performance purposes
	 */
	public static void cleanS3() {
		ObjectListing objectListing = s3Client.listObjects(TEST_S3_BUCKET_NAME);
		boolean firstTimeThrough = true;

		do {
			if (!firstTimeThrough) {
				objectListing = s3Client.listNextBatchOfObjects(objectListing);
			}

			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				s3Client.deleteObject(TEST_S3_BUCKET_NAME, objectSummary.getKey());
			}


			firstTimeThrough = false;
		} while (objectListing.isTruncated());
	}
}
