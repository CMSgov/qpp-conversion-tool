package gov.cms.qpp.conversion.api.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.given;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;

import gov.cms.qpp.conversion.api.services.MetadataRepository;
import gov.cms.qpp.test.annotations.AcceptanceTest;
import gov.cms.qpp.test.helper.AwsTestHelper;

@ExtendWith(RestExtension.class)
@ExtendWith(SpringExtension.class)
class QrdaApiAcceptance {

	private static final String QRDA_API_PATH = "/";
	private static final String MULTIPART_FORM_DATA_KEY = "file";
	private static final String TEST_S3_BUCKET_NAME = "qpp-qrda3converter-acceptance-test";

	private long beforeObjectCount;
	private long beforeDynamoCount;

	@Inject
	private MetadataRepository repository;

	@BeforeEach
	void beforeCounts() {
		repository.deleteAll();

		beforeObjectCount = getS3ObjectCount();
		beforeDynamoCount = getDatabaseItemCount();
	}

	@AfterEach
	void after() {
		repository.deleteAll();
	}

	@AcceptanceTest
	void testWithValid() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Paths.get("../qrda-files/valid-QRDA-III-latest.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(201);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDatabaseItemCount();

		assertThat(afterObjectCount).isEqualTo(beforeObjectCount + 2);
		assertThat(afterDynamoCount).isEqualTo(beforeDynamoCount + 1);
	}

	@AcceptanceTest
	void testWithConversionError() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Paths.get("../qrda-files/not-a-QDRA-III-file.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(422);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDatabaseItemCount();

		assertThat(afterObjectCount).isEqualTo(beforeObjectCount + 2);
		assertThat(afterDynamoCount).isEqualTo(beforeDynamoCount + 1);
	}

	@AcceptanceTest
	void testWithValidationError() {
		given()
			.multiPart(MULTIPART_FORM_DATA_KEY, Paths.get("../rest-api/src/test/resources/fail_validation.xml").toFile())
			.when()
			.post(QRDA_API_PATH)
			.then()
			.statusCode(422);

		long afterObjectCount = getS3ObjectCount();
		long afterDynamoCount = getDatabaseItemCount();

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

	private long getDatabaseItemCount() {
		return repository.count();
	}
}
