package gov.cms.qpp.conversion.api.acceptance;

import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.put;

@ExtendWith(RestExtension.class)
class CpcApiAcceptance {

	private static final String CPC_UNPROCESSED_FILES_API_PATH = "/cpc/unprocessed-files";
	private static final String CPC_FILE_API_PATH = "/cpc/file/";

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
			.contentType("application/xml");
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

		int numberOfUnprocessedFiles = unprocessedFiles.size();
		String firstFileId = (String)unprocessedFiles.get(0).get("fileId");

		String responseBody = markFileAsProcessed(firstFileId, 200);

		assertThat(responseBody).isEqualTo("The file was found and will be updated as processed.");
		assertThat(getUnprocessedFiles().size()).isEqualTo(numberOfUnprocessedFiles - 1);
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

		String responseBody = markFileAsProcessed("c9368ae7-474d-4106-919e-be94d862875f", 404);
		assertThat(responseBody).isEqualTo("The file was not a CPC+ file.");
	}

	private String getFirstUnprocessedCpcFileId() {
		return (String)getUnprocessedFiles().get(0).get("fileId");
	}

	private String markFileAsProcessed(String fileId, int expectedResponseCode) {
		return given()
			.auth().oauth2(createCpcJwtToken())
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
