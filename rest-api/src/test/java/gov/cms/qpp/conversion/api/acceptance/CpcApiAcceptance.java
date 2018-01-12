package gov.cms.qpp.conversion.api.acceptance;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;
import gov.cms.qpp.conversion.api.model.CpcFileStatusUpdateRequest;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.put;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;

@ExtendWith(RestExtension.class)
class CpcApiAcceptance {

	private static final String CPC_UNPROCESSED_FILES_API_PATH = "/cpc/unprocessed-files";
	private static final String CPC_FILE_API_PATH = "/cpc/file/";
	private static final String PROGRAM_NAME_XPATH = "/*[local-name() = 'ClinicalDocument' and namespace-uri() = 'urn:hl7-org:v3']"
		+ "/./*[local-name() = 'informationRecipient' and namespace-uri() = 'urn:hl7-org:v3']"
		+ "/*[local-name() = 'intendedRecipient' and namespace-uri() = 'urn:hl7-org:v3']"
		+ "/*[local-name() = 'id' and namespace-uri() = 'urn:hl7-org:v3'][@root='2.16.840.1.113883.3.249.7']/@extension";
	private static final String CPC_PLUS_PROGRAM_NAME = "CPCPLUS";
	private static final String NOT_A_CPC_FILE = "beed7ed4-107c-400f-b0de-0c60abc54344";

	@BeforeAll
	static void createUnprocessedItem() {
		given()
			.multiPart("file", Paths.get("../sample-files/CPCPlus_Success_PreProd.xml").toFile())
			.when()
			.post("/");
	}

	@Test
	@Tag("acceptance")
	void testNoSecurityForUnprocessedFiles() {
		get(CPC_UNPROCESSED_FILES_API_PATH)
			.then()
			.statusCode(403);
	}

	@Test
	@Tag("acceptance")
	void testUnprocessedFiles() {

		List<Map> responseBody = getUnprocessedFiles();

		assertThat(responseBody).isNotEmpty();
		assertThat(responseBody.get(0)).containsKey("fileId");
		assertThat(responseBody.get(0)).containsKey("filename");
		assertThat(responseBody.get(0)).containsKey("apm");
		assertThat(responseBody.get(0)).containsKey("conversionDate");
		assertThat(responseBody.get(0)).containsKey("validationSuccess");
	}

	@Test
	@Tag("acceptance")
	void testNoSecurityGetFile() {

		String firstFileId = getFirstUnprocessedCpcFileId();

		get(CPC_FILE_API_PATH + firstFileId)
			.then()
			.statusCode(403);
	}

	@Test
	@Tag("acceptance")
	void testGetFile() {

		String firstFileId = getFirstUnprocessedCpcFileId();

		given()
			.auth().oauth2(createCpcJwtToken())
			.get(CPC_FILE_API_PATH + firstFileId)
			.then()
			.statusCode(200)
			.contentType("application/xml")
			.body(hasXPath(PROGRAM_NAME_XPATH, equalTo(CPC_PLUS_PROGRAM_NAME)));
	}

	@Test
	@Tag("acceptance")
	void testNoSecurityMarkFileProcessed() {

		String firstFileId = getFirstUnprocessedCpcFileId();

		put(CPC_FILE_API_PATH + firstFileId)
			.then()
			.statusCode(403);
	}

	@Test
	@Tag("acceptance")
	void testMarkFileProcessed() {

		List<Map> unprocessedFiles = getUnprocessedFiles();
		String firstFileId = (String)unprocessedFiles.get(0).get("fileId");

		String responseBody = markFileAsProcessed(firstFileId, 200);

		assertThat(responseBody).isEqualTo("The file was found and will be updated as processed.");
		assertThat(getUnprocessedFiles().stream().filter(metadata -> metadata.get("fileId").equals(firstFileId)).count()).isEqualTo(0);
	}

	@Test
	@Tag("acceptance")
	void testMarkFileProcessedBadFileId() {

		String responseBody = markFileAsProcessed("Moof!", 404);
		assertThat(responseBody).isEqualTo("File not found!");
	}

	@Test
	@Tag("acceptance")
	void testMarkFileProcessedNotCPC() {

		String responseBody = markFileAsProcessed(NOT_A_CPC_FILE, 404);
		assertThat(responseBody).isEqualTo("The file was not a CPC+ file.");
	}

	@Test
	@Tag("acceptance")
	void testMarkFileUnProcessed() {

		List<Map> unprocessedFiles = getUnprocessedFiles();
		String firstFileId = (String)unprocessedFiles.get(0).get("fileId");

		String responseBody = markFileAsUnProcessed(firstFileId, 200);

		assertThat(responseBody).isEqualTo("The file was found and will be updated as unprocessed.");
		assertThat(getUnprocessedFiles().stream().filter(metadata -> metadata.get("fileId").equals(firstFileId)).count()).isEqualTo(1);
	}

	private String getFirstUnprocessedCpcFileId() {
		return (String)getUnprocessedFiles().get(0).get("fileId");
	}

	private String markFileAsProcessed(String fileId, int expectedResponseCode) {
		return markFile(fileId, true, expectedResponseCode);
	}

	private String markFileAsUnProcessed(String fileId, int expectedResponseCode) {
		return markFile(fileId, false, expectedResponseCode);
	}

	private String markFile(String fileId, boolean processed, int expectedResponseCode) {
		CpcFileStatusUpdateRequest status = new CpcFileStatusUpdateRequest();
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
}
