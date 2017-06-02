package gov.cms.qpp.conversion;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class ConversionFileWriterWrapperTest {

	@After
	public void deleteFiles() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
		Files.deleteIfExists(Paths.get("not-a-QRDA-III-file.err.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.qpp.json"));
	}

	@Test
	public void testValidQpp() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.doDefaults(false).transform();

		assertFileExists("valid-QRDA-III.qpp.json");
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
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileDoesNotExists("valid-QRDA-III.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteErrors() throws IOException {
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileDoesNotExists("not-a-QRDA-III-file.err.json");
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