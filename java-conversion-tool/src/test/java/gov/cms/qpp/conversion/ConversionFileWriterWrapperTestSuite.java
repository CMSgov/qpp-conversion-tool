package gov.cms.qpp.conversion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.util.ExceptionHelper;

public class ConversionFileWriterWrapperTestSuite extends ConversionTestSuite {

	@After
	public final void cleanupTestFiles() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
		Files.deleteIfExists(Paths.get("not-a-QRDA-III-file.err.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.qpp.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.err.json"));
	}

	protected final void assertFileDoesNotExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertFalse("The file " + fileName + " must NOT exist.", Files.exists(possibleFile));
	}

	protected final void assertFileExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertTrue("The file " + fileName + " must exist.", Files.exists(possibleFile));
	}

	protected final void transform(ConversionFileWriterWrapper conversion) {
		ExceptionHelper.runOrPropagate(conversion.transform());
	}

}