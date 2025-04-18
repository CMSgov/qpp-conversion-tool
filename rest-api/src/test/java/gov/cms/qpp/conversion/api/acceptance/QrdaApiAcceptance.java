package gov.cms.qpp.conversion.api.acceptance;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import gov.cms.qpp.test.annotations.AcceptanceTest;
import gov.cms.qpp.test.helper.AwsTestHelper;

import java.nio.file.Path;

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
		AmazonS3 s3Client = AwsTestHelper.getS3Client();

		ObjectListing objectListing = s3Client.listObjects(TEST_S3_BUCKET_NAME);
		long objectCount = objectListing.getObjectSummaries().size();

		while(objectListing.isTruncated()) {
			objectListing = s3Client.listNextBatchOfObjects(objectListing);
			objectCount += objectListing.getObjectSummaries().size();
		}

		return objectCount;
	}

	private long getDynamoItemCount() {
		AmazonDynamoDB dynamoClient = AwsTestHelper.getDynamoClient();

		ScanResult scanResult = dynamoClient.scan(AwsTestHelper.TEST_DYNAMO_TABLE_NAME, Lists.newArrayList("Uuid"));
		long itemCount = scanResult.getCount();

		while(scanResult.getLastEvaluatedKey() != null && !scanResult.getLastEvaluatedKey().isEmpty()) {
			scanResult = dynamoClient.scan(new ScanRequest().withTableName(AwsTestHelper.TEST_DYNAMO_TABLE_NAME).withAttributesToGet("Uuid").withExclusiveStartKey(scanResult.getLastEvaluatedKey()));
			itemCount += scanResult.getCount();
		}

		return itemCount;
	}
}
