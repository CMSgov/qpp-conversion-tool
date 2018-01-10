package gov.cms.qpp.conversion.api.acceptance;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.given;

@ExtendWith(RestExtension.class)
class QrdaApiAcceptance {

	private static final String QRDA_API_PATH = "/";
	private static final String MULTIPART_FORM_DATA_KEY = "file";
	private static final String TEST_S3_BUCKET_NAME = "flexion-qpp-conversion-tool-pii-convrtr-audt-test-us-east-1";
	private static final String TEST_DYNAMO_TABLE_NAME = "qpp-qrda3converter-test-metadata";

	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

	private AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

	private long beforeObjectCount;
	private long beforeDynamoCount;

	@BeforeEach
	void beforeCounts() {
		beforeObjectCount = getS3ObjectCount();
		beforeDynamoCount = getDynamoItemCount();
	}

	@Test
	@Tag("acceptance")
	void testWithValid() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Paths.get("../qrda-files/valid-QRDA-III-latest.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(201);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDynamoItemCount();

		assertThat(afterObjectCount).isEqualTo(beforeObjectCount + 2);
		assertThat(afterDynamoCount).isEqualTo(beforeDynamoCount + 1);
	}

	@Test
	@Tag("acceptance")
	void testWithConversionError() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Paths.get("../qrda-files/not-a-QDRA-III-file.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(422);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDynamoItemCount();

		assertThat(afterObjectCount).isEqualTo(beforeObjectCount + 2);
		assertThat(afterDynamoCount).isEqualTo(beforeDynamoCount + 1);
	}

	@Test
	@Tag("acceptance")
	void testWithValidationError() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Paths.get("../rest-api/src/test/resources/fail_validation.xml").toFile())
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

		ObjectListing objectListing = s3Client.listObjects(TEST_S3_BUCKET_NAME);
		long objectCount = objectListing.getObjectSummaries().size();

		while(objectListing.isTruncated()) {
			objectListing = s3Client.listNextBatchOfObjects(objectListing);
			objectCount += objectListing.getObjectSummaries().size();
		}

		return objectCount;
	}

	private long getDynamoItemCount() {
		return dynamoClient.scan(TEST_DYNAMO_TABLE_NAME, Lists.newArrayList("Uuid")).getCount();
	}
}
