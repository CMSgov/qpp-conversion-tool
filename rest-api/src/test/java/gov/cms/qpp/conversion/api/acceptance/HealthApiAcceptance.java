package gov.cms.qpp.conversion.api.acceptance;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.get;


@ExtendWith(RestExtension.class)
class HealthApiAcceptance {
	@Test
	@Tag("acceptance")
	void apiResponsesWithOkStatus() {
		get("/health").then().statusCode(200);
	}
}