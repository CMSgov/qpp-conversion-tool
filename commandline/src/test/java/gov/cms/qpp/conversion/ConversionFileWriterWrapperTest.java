package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

		converterWrapper.doDefaults(false).transform();

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

		converterWrapper.doValidation(false).transform();

		assertFileExists("qrda_bad_denominator.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteQpp() throws IOException {
		PowerMockito.mockStatic(Files.class);
		PowerMockito.when(Files.newBufferedWriter(Matchers.any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileDoesNotExists("valid-QRDA-III-latest.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteErrors() throws IOException {
		PowerMockito.mockStatic(Files.class);
		PowerMockito.when(Files.newBufferedWriter(Matchers.any(Path.class))).thenThrow(new IOException());

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
		converterWrapper.transform();
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
		converterWrapper.transform();
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

	private void assertFileExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertTrue("The file " + fileName + " must exist.", Files.exists(possibleFile));
	}

	private void assertFileDoesNotExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertFalse("The file " + fileName + " must NOT exist.", Files.exists(possibleFile));
	}
}