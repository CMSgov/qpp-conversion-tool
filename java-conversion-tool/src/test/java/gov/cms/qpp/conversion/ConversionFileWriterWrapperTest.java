package gov.cms.qpp.conversion;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

import gov.cms.qpp.conversion.util.JsonHelper;

public class ConversionFileWriterWrapperTest extends ConversionFileWriterWrapperTestSuite {

	@Test
	public void testValidQpp() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path)
				.doDefaults(false);

		transform(converterWrapper);

		assertFileExists("valid-QRDA-III.qpp.json");
	}

	@Test
	public void testInvalidQpp() {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		transform(converterWrapper);

		assertFileExists("not-a-QRDA-III-file.err.json");
	}

	@Test
	public void testSkipValidations() {
		Path path = Paths.get("src/test/resources/qrda_bad_denominator.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path)
				.doValidation(false);

		transform(converterWrapper);

		assertFileExists("qrda_bad_denominator.qpp.json");
	}

	@Test
	public void testErrorHasSourceId() throws IOException {
		//when
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		transform(converterWrapper);

		//then
		String sourceId = JsonHelper.readJsonAtJsonPath(Paths.get("not-a-QRDA-III-file.err.json"),
				"$.errors[0].sourceIdentifier", String.class);

		assertThat("Must contain a source identifier", sourceId, is("not-a-QRDA-III-file.xml"));
	}

	@Test
	public void testErrorHasDetail() throws IOException {
		//setup
		String errorMessage = "The file is not a QRDA-III XML document";
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");

		//when
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		transform(converterWrapper);
		Map<String, String> detail = JsonHelper.readJsonAtJsonPath(Paths.get("not-a-QRDA-III-file.err.json"),
				"$.errors[0].details[0]");

		//then
		assertThat("Contains detail message", detail.get("message"), is(errorMessage));
		assertTrue("Contains detail path", detail.get("path").isEmpty());
	}

	@Test
	public void testErrorHasMultipleDetails() throws IOException {
		//setup
		String firstMessage = "This Numerator Node Aggregate Value has an invalid value";
		String secondMessage = "This Denominator Node Aggregate Value has an invalid value";
		Path path = Paths.get("src/test/resources/qrda_bad_denominator.xml");

		//when
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		transform(converterWrapper);
		Map<String, String> firstDetail = JsonHelper.readJsonAtJsonPath(Paths.get("qrda_bad_denominator.err.json"),
				"$.errors[0].details[0]");
		Map<String, String> secondDetail = JsonHelper.readJsonAtJsonPath(Paths.get("qrda_bad_denominator.err.json"),
				"$.errors[0].details[1]");

		//then
		assertThat("Contains detail message", firstDetail.get("message"), is(firstMessage));
		assertNotNull("Contains detail path", firstDetail.get("path"));
		assertThat("Contains detail message", secondDetail.get("message"), is(secondMessage));
		assertNotNull("Contains detail path", secondDetail.get("path"));
	}

}