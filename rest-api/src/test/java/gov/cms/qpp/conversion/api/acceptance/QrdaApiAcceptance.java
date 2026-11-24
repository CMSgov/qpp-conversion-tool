package gov.cms.qpp.conversion.api.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import gov.cms.qpp.test.annotations.AcceptanceTest;
import gov.cms.qpp.test.helper.AwsTestHelper;

import java.nio.file.Path;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.given;

@ExtendWith(RestExtension.class)
class QrdaApiAcceptance {

	private static final String QRDA_API_PATH = "/";
	private static final String MULTIPART_FORM_DATA_KEY = "file";
	private static final String TEST_S3_BUCKET_NAME = "qpp-qrda3converter-acceptance-test";

	private long beforeObjectCount;
	private long beforeDynamoCount;

	@BeforeEach
	void beforeCounts() {
		beforeObjectCount = getS3ObjectCount();
		beforeDynamoCount = getDynamoItemCount();
	}

	@AcceptanceTest
	void testWithValid() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Path.of("../qrda-files/valid-QRDA-III-latest.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(201);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDynamoItemCount();

		assertThat(afterObjectCount).isEqualTo(beforeObjectCount + 2);
		assertThat(afterDynamoCount).isEqualTo(beforeDynamoCount + 1);
	}

	@AcceptanceTest
	void testWithConversionError() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Path.of("../qrda-files/not-a-QDRA-III-file.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(422);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDynamoItemCount();

		assertThat(afterObjectCount).isEqualTo(beforeObjectCount + 2);
		assertThat(afterDynamoCount).isEqualTo(beforeDynamoCount + 1);
	}

	@AcceptanceTest
	void testWithValidationError() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Path.of("../rest-api/src/test/resources/fail_validation.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(422);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDynamoItemCount();

		assertThat(afterObjectCount).isEqualTo(beforeObjectCount + 4);
		assertThat(afterDynamoCount).isEqualTo(beforeDynamoCount + 1);
	}

	private long getS3ObjectCount() {
		try (S3Client s3Client = AwsTestHelper.getS3Client()) {
			long objectCount = 0;
			String continuationToken = null;
			do {
				ListObjectsV2Request.Builder request = ListObjectsV2Request.builder().bucket(TEST_S3_BUCKET_NAME);
				if (continuationToken != null) {
					request.continuationToken(continuationToken);
				}
				ListObjectsV2Response response = s3Client.listObjectsV2(request.build());
				objectCount += response.contents().size();
				continuationToken = response.nextContinuationToken();
			} while (continuationToken != null);
			return objectCount;
		}
	}

	private long getDynamoItemCount() {
		try (DynamoDbClient dynamoClient = AwsTestHelper.getDynamoClient()) {
			long itemCount = 0;
			java.util.Map<String, AttributeValue> startKey = java.util.Map.of();
			do {
				ScanRequest.Builder request = ScanRequest.builder()
					.tableName(AwsTestHelper.TEST_DYNAMO_TABLE_NAME)
					.projectionExpression("Uuid");
				if (!startKey.isEmpty()) {
					request.exclusiveStartKey(startKey);
				}
				ScanResponse response = dynamoClient.scan(request.build());
				itemCount += response.count();
				startKey = response.lastEvaluatedKey();
			} while (startKey != null && !startKey.isEmpty());
			return itemCount;
		}
	}
}
