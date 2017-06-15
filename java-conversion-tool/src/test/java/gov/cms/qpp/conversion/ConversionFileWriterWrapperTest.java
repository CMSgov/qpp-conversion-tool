package gov.cms.qpp.conversion;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.util.JsonHelper;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class ConversionFileWriterWrapperTest extends ConversionTestSuite {

	@After
	public void deleteFiles() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
		Files.deleteIfExists(Paths.get("not-a-QRDA-III-file.err.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.qpp.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.err.json"));
	}

	@Test
	public void testValidQpp() throws Exception {
		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.doDefaults(false).transform().call();

		assertFileExists("valid-QRDA-III.qpp.json");
	}

	@Test
	public void testInvalidQpp() throws Exception {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform().call();

		assertFileExists("not-a-QRDA-III-file.err.json");
	}

	@Test
	public void testSkipValidations() throws Exception {
		Path path = Paths.get("src/test/resources/qrda_bad_denominator.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.doValidation(false).transform().call();

		assertFileExists("qrda_bad_denominator.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteQpp() throws Exception {
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform().call();

		assertFileDoesNotExists("valid-QRDA-III.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteErrors() throws Exception {
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform().call();

		assertFileDoesNotExists("not-a-QRDA-III-file.err.json");
	}

	@Test
	public void testErrorHasSourceId() throws Exception {
		//when
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		converterWrapper.transform().call();

		//then
		String sourceId = JsonHelper.readJsonAtJsonPath(Paths.get("not-a-QRDA-III-file.err.json"),
				"$.errors[0].sourceIdentifier", String.class);

		assertThat("Must contain a source identifier", sourceId, is("not-a-QRDA-III-file.xml"));
	}

	@Test
	public void testErrorHasDetail() throws Exception {
		//setup
		String errorMessage = "The file is not a QRDA-III XML document";
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");

		//when
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		converterWrapper.transform().call();
		Map<String, String> detail = JsonHelper.readJsonAtJsonPath(Paths.get("not-a-QRDA-III-file.err.json"),
				"$.errors[0].details[0]");

		//then
		assertThat("Contains detail message", detail.get("message"), is(errorMessage));
		assertTrue("Contains detail path", detail.get("path").isEmpty());
	}

	@Test
	public void testErrorHasMultipleDetails() throws Exception {
		System.setOut(console());
		System.out.println("RUNNING STUPID TEST");
		//setup
		String firstMessage = "This Numerator Node Aggregate Value has an invalid value";
		String secondMessage = "This Denominator Node Aggregate Value has an invalid value";
		Path path = Paths.get("src/test/resources/qrda_bad_denominator.xml");

		//when
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		System.out.println("CALLING TRANSFORM");
		boolean r = converterWrapper.transform().call();
		console().println("DONE: " + r);
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