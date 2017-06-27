package gov.cms.qpp.conversion.api.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.api.model.ErrorMessage;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class ValidationServiceImplTest {
	private static Path path;
	private static JsonWrapper wrapper;
	private static AllErrors errors;
	private static ErrorMessage message;


	@BeforeClass
	public static void setup() throws IOException {
		path = Paths.get("src/test/resources/submissionErrorFixture.json");
		Path toConvert = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		wrapper = new JsonWrapper(new Converter(toConvert).transform(), false);
		prepAllErrors();
	}

	private static void prepAllErrors() throws IOException {
		ValidationServiceImpl service = new ValidationServiceImpl();
		message = JsonHelper.readJsonAtJsonPath(
				path, "$.invalidMeasureId", ErrorMessage.class);

		String errorJson = new ObjectMapper().writeValueAsString(message);
		errors = service.convertQppValidationErrorsToQrda(errorJson, wrapper);
	}

	@Test
	public void testJsonDeserialization() {
		assertThat("Error json should map to AllErrors", errors.getErrors(), hasSize(1));
	}

	@Test
	public void testQppToQrdaErrorPathConversion() {
		Detail detail = message.getError().getDetails().get(0);
		Detail mappedDetails = errors.getErrors().get(0).getDetails().get(0);

		assertNotEquals("Json path should be converted to xpath",
				detail.getPath(), mappedDetails.getPath());
	}

}
