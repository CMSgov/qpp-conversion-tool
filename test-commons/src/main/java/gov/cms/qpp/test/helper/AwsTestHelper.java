package gov.cms.qpp.test.helper;

import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

/**
 * A helper class for AWS testing
 */
public class AwsTestHelper {
	public static final String TEST_DYNAMO_TABLE_NAME = "qpp-qrda3converter-acceptance-test";
	public static final String TEST_S3_BUCKET_NAME  = "qpp-qrda3converter-acceptance-test";

	// Shared region constant
	private static final Region REGION = Region.US_EAST_1;

	/**
	 * Returns a new DynamoDB client for test operations.
	 * (Eliminates MS_EXPOSE_REP by not exposing internal static fields.)
	 */
	public static DynamoDbClient getDynamoClient() {
		return DynamoDbClient.builder()
				.region(REGION)
				.build();
	}

	/**
	 * Returns a new S3 client for test operations.
	 */
	public static S3Client getS3Client() {
		return S3Client.builder()
				.region(REGION)
				.build();
	}

	/**
	 * Returns a new KMS client for test operations.
	 */
	public static KmsClient getKmsClient() {
		return KmsClient.builder()
				.region(REGION)
				.build();
	}

	/**
	 * Cleans DynamoDB items via batch scan and delete for performance.
	 */
	public static void cleanDynamoDb() {
		try (DynamoDbClient client = getDynamoClient()) {
			Map<String, AttributeValue> startKey = Map.of();
			do {
				ScanRequest.Builder scanRequest = ScanRequest.builder()
					.tableName(TEST_DYNAMO_TABLE_NAME)
					.projectionExpression("Uuid");
				if (!startKey.isEmpty()) {
					scanRequest.exclusiveStartKey(startKey);
				}
				ScanResponse response = client.scan(scanRequest.build());
				for (Map<String, AttributeValue> item : response.items()) {
					client.deleteItem(DeleteItemRequest.builder()
						.tableName(TEST_DYNAMO_TABLE_NAME)
						.key(Map.of("Uuid", item.get("Uuid")))
						.build());
				}
				startKey = response.lastEvaluatedKey();
			} while (startKey != null && !startKey.isEmpty());
		}
	}

	/**
	 * Cleans up the test S3 bucket by batch for performance purposes.
	 */
	public static void cleanS3() {
		try (S3Client client = getS3Client()) {
			String continuationToken = null;
			do {
				ListObjectsV2Request.Builder request = ListObjectsV2Request.builder()
					.bucket(TEST_S3_BUCKET_NAME);
				if (continuationToken != null) {
					request.continuationToken(continuationToken);
				}
				ListObjectsV2Response response = client.listObjectsV2(request.build());
				response.contents().forEach(object -> client.deleteObject(DeleteObjectRequest.builder()
					.bucket(TEST_S3_BUCKET_NAME)
					.key(object.key())
					.build()));
				continuationToken = response.nextContinuationToken();
			} while (continuationToken != null);
		}
	}
}
