package gov.cms.qpp.conversion.api.acceptance;

import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.cms.qpp.conversion.api.config.DynamoDbConfigFactory;
import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.util.EnvironmentHelper;
import gov.cms.qpp.test.annotations.AcceptanceTest;
import gov.cms.qpp.test.helper.AwsTestHelper;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.spotify.hamcrest.jackson.IsJsonArray.jsonArray;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonInt;
import static com.spotify.hamcrest.jackson.IsJsonObject.jsonObject;
import static com.spotify.hamcrest.jackson.IsJsonText.jsonText;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.put;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.hamcrest.xml.HasXPath.hasXPath;

@ExtendWith(RestExtension.class)
class CpcApiAcceptance {
	private static final String CPC_UNPROCESSED_FILES_API_PATH = "/cpc/unprocessed-files";
	private static final String CPC_FILE_API_PATH = "/cpc/file/";
	private static final String CPC_QPP_API_PATH = "/cpc/qpp/";
	private static final String PROGRAM_NAME_XPATH = "/*[local-name() = 'ClinicalDocument' and namespace-uri() = 'urn:hl7-org:v3']"
		+ "/./*[local-name() = 'informationRecipient' and namespace-uri() = 'urn:hl7-org:v3']"
		+ "/*[local-name() = 'intendedRecipient' and namespace-uri() = 'urn:hl7-org:v3']"
		+ "/*[local-name() = 'id' and namespace-uri() = 'urn:hl7-org:v3'][@root='2.16.840.1.113883.3.249.7']/@extension";
	private static final String CPC_PLUS_PROGRAM_NAME = "CPCPLUS";
	private static DynamoDBMapper mapper;
	private static final String TEST_KMS_KEY_ENV_VARIABLE = "TEST_KMS_KEY";
	private static final String DEFAULT_KMS_ARN = "arn:aws:kms:us-east-1:850094054452:key/8b19f7e9-b58c-4b7a-8162-e01809a8a2e9";

	@BeforeAll
	static void setUp() {
		String kmsKey = EnvironmentHelper.getOrDefault(TEST_KMS_KEY_ENV_VARIABLE, DEFAULT_KMS_ARN);
		mapper = DynamoDbConfigFactory
			.createDynamoDbMapper(AwsTestHelper.getDynamoClient(), DynamoDBMapperConfig.builder()
				.withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(AwsTestHelper.TEST_DYNAMO_TABLE_NAME))
				.build(), new AttributeEncryptor(new DirectKmsMaterialProvider(AwsTestHelper.getKmsClient(), kmsKey)));

		given()
			.multiPart("file", Paths.get("../sample-files/CPCPlus_Success_PreProd.xml").toFile())
			.when()
			.post("/");
	}

	@AcceptanceTest
	void testNoSecurityForUnprocessedFiles() {
		get(CPC_UNPROCESSED_FILES_API_PATH)
			.then()
			.statusCode(403);
	}

	@AcceptanceTest
	void testUnprocessedFiles() {

		List<Map> responseBody = getUnprocessedFiles();

		assertThat(responseBody).isNotEmpty();
		assertThat(responseBody.get(0)).containsKey("fileId");
		assertThat(responseBody.get(0)).containsKey("filename");
		assertThat(responseBody.get(0)).containsKey("apm");
		assertThat(responseBody.get(0)).containsKey("conversionDate");
		assertThat(responseBody.get(0)).containsKey("validationSuccess");
	}

	@AcceptanceTest
	void testUnprocessedFilesDates() {
		Metadata afterJanuarySecondMetadata = createDatedCpcMetadata("2018-01-02T05:00:00.000Z");
		Metadata beforeJanuarySecondMetadata = createDatedCpcMetadata("2018-01-02T04:59:59.999Z");
		Metadata anotherAllowedMetadata = createDatedCpcMetadata("2018-02-26T14:36:43.723Z");
		Metadata anotherUnallowedMetadata = createDatedCpcMetadata("2017-12-25T00:00:00.000Z");

		mapper.batchSave(afterJanuarySecondMetadata, beforeJanuarySecondMetadata, anotherAllowedMetadata, anotherUnallowedMetadata);

		List<Map> responseBody = getUnprocessedFiles();

		responseBody.stream().forEach(map ->
			assertThat(Instant.parse((String)map.get("conversionDate")))
				.isGreaterThan(Instant.parse("2018-01-02T04:59:59.999Z")));
	}

	@AcceptanceTest
	void testNoSecurityGetFile() {

		String firstFileId = getFirstUnprocessedCpcFileId();

		get(CPC_FILE_API_PATH + firstFileId)
			.then()
			.statusCode(403);
	}

	@AcceptanceTest
	void testGetFile() {

		String firstFileId = getFirstUnprocessedCpcFileId();

		given()
			.auth().oauth2(createCpcJwtToken())
			.get(CPC_FILE_API_PATH + firstFileId)
			.then()
			.statusCode(200)
			.contentType("application/xml")
			.body(hasXPath(PROGRAM_NAME_XPATH, equalToIgnoringCase(CPC_PLUS_PROGRAM_NAME)));
	}

	@AcceptanceTest
	void testGetQpp() throws IOException {

		String firstFileId = getFirstUnprocessedCpcFileId();

		String payload = given()
			.auth().oauth2(createCpcJwtToken())
			.get(CPC_QPP_API_PATH + firstFileId)
			.then()
			.statusCode(200)
			.contentType("application/json")
			.extract()
			.body()
			.asString();

		verifyJson(payload, jsonObject()
			.where("performanceYear", jsonInt(2018))
			.where("taxpayerIdentificationNumber", jsonText("000000099"))
			.where("nationalProviderIdentifier", jsonText("0007891421"))
			.where("measurementSets", jsonArray()));
	}

	@AcceptanceTest
	void testNoSecurityGetQpp() {

		String firstFileId = getFirstUnprocessedCpcFileId();

		get(CPC_QPP_API_PATH + firstFileId)
			.then()
			.statusCode(403);
	}

	@AcceptanceTest
	void testNoSecurityMarkFileProcessed() {

		String firstFileId = getFirstUnprocessedCpcFileId();

		put(CPC_FILE_API_PATH + firstFileId)
			.then()
			.statusCode(403);
	}

	@AcceptanceTest
	void testMarkFileProcessed() {

		List<Map> unprocessedFiles = getUnprocessedFiles();
		String firstFileId = (String) unprocessedFiles.get(0).get("fileId");

		String responseBody = markFileAsProcessed(firstFileId, 200);

		assertThat(responseBody).isEqualTo("The file was found and will be updated as processed.");
		assertThat(getUnprocessedFiles().stream().filter(metadata -> metadata.get("fileId").equals(firstFileId)).count()).isEqualTo(0);
	}

	@AcceptanceTest
	void testMarkFileProcessedBadFileId() {

		String responseBody = markFileAsProcessed("Moof!", 404);
		assertThat(responseBody).isEqualTo("File not found!");
	}

	@AcceptanceTest
	void testMarkFileProcessedNotCPC() {
		Metadata metadata = createDatedCpcMetadata("2018-01-02T05:00:00.000Z");
		metadata.setCpc(null);
		mapper.save(metadata);

		String responseBody = markFileAsProcessed(metadata.getUuid(), 404);
		assertThat(responseBody).isEqualTo("The file was not a CPC+ file.");
	}

	@AcceptanceTest
	void testMarkFileUnProcessed() {

		List<Map> unprocessedFiles = getUnprocessedFiles();
		String firstFileId = (String) unprocessedFiles.get(0).get("fileId");

		String responseBody = markFileAsUnProcessed(firstFileId, 200);

		assertThat(responseBody).isEqualTo("The file was found and will be updated as unprocessed.");
		assertThat(getUnprocessedFiles().stream().filter(metadata -> metadata.get("fileId").equals(firstFileId)).count()).isEqualTo(1);
	}

	private void verifyJson(String json, Matcher<JsonNode> matcher) throws IOException {
		ObjectNode node = (ObjectNode) (new ObjectMapper()).readTree(json);
		StringDescription description = new StringDescription();
		description.appendDescriptionOf(matcher);

		assertWithMessage("Expected: " + description.toString() + " Actual: " + json)
			.that(matcher.matches(node))
			.isTrue();
	}

	private String getFirstUnprocessedCpcFileId() {
		return (String) getUnprocessedFiles().get(0).get("fileId");
	}

	private String markFileAsProcessed(String fileId, int expectedResponseCode) {
		return markFile(fileId, true, expectedResponseCode);
	}

	private String markFileAsUnProcessed(String fileId, int expectedResponseCode) {
		return markFile(fileId, false, expectedResponseCode);
	}

	private String markFile(String fileId, boolean processed, int expectedResponseCode) {
		FileStatusUpdateRequest status = new FileStatusUpdateRequest();
		status.setProcessed(processed);

		return given()
			.auth().oauth2(createCpcJwtToken())
			.contentType("application/json").body(status)
			.when()
			.put(CPC_FILE_API_PATH + fileId)
			.then()
			.statusCode(expectedResponseCode)
			.contentType("text/plain")
			.extract()
			.body().asString();
	}

	private List<Map> getUnprocessedFiles() {
		return given()
			.auth().oauth2(createCpcJwtToken())
			.get(CPC_UNPROCESSED_FILES_API_PATH)
			.then()
			.statusCode(200)
			.extract()
			.body().jsonPath().getList("$", Map.class);
	}

	private String createCpcJwtToken() {
		JwtPayloadHelper payload = new JwtPayloadHelper()
			.withName("cpc-test")
			.withOrgType("registry");

		return JwtTestHelper.createJwt(payload);
	}

	private Metadata createDatedCpcMetadata(String parsableDate) {
		Metadata metadata = Metadata.create();
		metadata.setApm("T3STV47U3");
		metadata.setTin("0001233212");
		metadata.setNpi("012123123");
		metadata.setCpc(Constants.CPC_DYNAMO_PARTITION_START + "15");
		metadata.setCpcProcessed(false);
		metadata.setFileName("acceptance_test.xml");
		metadata.setConversionStatus(true);
		metadata.setOverallStatus(true);
		metadata.setValidationStatus(true);
		metadata.setQppLocator("not-here?");
		metadata.setValidationErrorLocator("not-there?");
		metadata.setConversionErrorLocator("not-anywhere?");
		metadata.setCreatedDate(Instant.parse(parsableDate));

		return metadata;
	}
}
