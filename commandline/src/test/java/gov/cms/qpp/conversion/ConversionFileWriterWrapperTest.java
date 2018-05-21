package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.test.helper.JsonTestHelper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

@RunWith(PowerMockRunner.class)
public class ConversionFileWriterWrapperTest {

	@After
	public void deleteFiles() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III-latest.qpp.json"));
		Files.deleteIfExists(Paths.get("not-a-QRDA-III-file.err.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.qpp.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.err.json"));
	}

	@Test
	public void testValidQpp() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		Context context = new Context();
		converterWrapper.setContext(context).transform();

		assertFileExists("valid-QRDA-III-latest.qpp.json");
	}

	@Test
	public void testInvalidQpp() {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileExists("not-a-QRDA-III-file.err.json");
	}

	@Test
	public void testSkipValidations() {
		Path path = Paths.get("src/test/resources/qrda_bad_denominator.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		Context context = new Context();
		context.setDoValidation(false);
		converterWrapper.setContext(context).transform();

		assertFileExists("qrda_bad_denominator.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteQpp() throws IOException {
		PowerMockito.mockStatic(Files.class);
		PowerMockito.when(Files.newBufferedWriter(ArgumentMatchers.any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileDoesNotExists("valid-QRDA-III-latest.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteErrors() throws IOException {
		PowerMockito.mockStatic(Files.class);
		PowerMockito.when(Files.newBufferedWriter(ArgumentMatchers.any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileDoesNotExists("not-a-QRDA-III-file.err.json");
	}

	@Test
	public void testErrorHasSourceId() throws IOException {
		//when
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		converterWrapper.transform();

		//then
		String sourceId = JsonTestHelper.readJsonAtJsonPath(Paths.get("not-a-QRDA-III-file.err.json"),
				"$.errors[0].sourceIdentifier", String.class);

		assertThat(sourceId)
				.isEqualTo("not-a-QRDA-III-file.xml");
	}

	@Test
	public void testErrorHasDetail() throws IOException {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");

		//when
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		converterWrapper.transform();
		Map<String, String> detail = JsonTestHelper.readJsonAtJsonPath(Paths.get("not-a-QRDA-III-file.err.json"),
				"$.errors[0].details[0]");

		//then
		assertThat(detail.get("message"))
				.isEqualTo(ErrorCode.NOT_VALID_QRDA_DOCUMENT.format(Context.REPORTING_YEAR, DocumentationReference.CLINICAL_DOCUMENT).getMessage());
		assertThat(detail.get("path"))
				.isEmpty();
	}

	@Test
	public void testErrorHasMultipleDetails() throws IOException {
		//setup
		LocalizedError firstError = ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format("Numerator", "-1");
		LocalizedError secondError = ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format("Denominator", "-1");
		Path path = Paths.get("src/test/resources/qrda_bad_denominator.xml");

		//when
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		converterWrapper.transform();
		Map<String, String> firstDetail = JsonTestHelper.readJsonAtJsonPath(Paths.get("qrda_bad_denominator.err.json"),
				"$.errors[0].details[0]");
		Map<String, String> secondDetail = JsonTestHelper.readJsonAtJsonPath(Paths.get("qrda_bad_denominator.err.json"),
				"$.errors[0].details[1]");

		JsonTestHelper.readJsonAtJsonPath(Paths.get("qrda_bad_denominator.err.json"), "$.errors[0].details");

		//then
		assertThat(firstDetail.get("message")).isEqualTo(firstError.getMessage());
		assertThat(firstDetail.get("path")).isNotNull();
		assertThat(secondDetail.get("message")).isEqualTo(secondError.getMessage());
		assertThat(secondDetail.get("path")).isNotNull();
	}

	private void assertFileExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertWithMessage("The file %s must exist.", fileName)
				.that(Files.exists(possibleFile))
				.isTrue();
	}

	private void assertFileDoesNotExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertWithMessage("The file %s must NOT exist.", fileName)
				.that(Files.exists(possibleFile))
				.isFalse();
	}
}