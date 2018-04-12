package gov.cms.qpp.conversion.api.acceptance;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

import gov.cms.qpp.test.annotations.AcceptanceTest;

import static io.restassured.RestAssured.get;

@ExtendWith(RestExtension.class)
class HealthApiAcceptance {

	@AcceptanceTest
	void testHttpStatusIsOk() {
		get("/health")
			.then()
			.statusCode(HttpStatus.OK.value());
	}
}
