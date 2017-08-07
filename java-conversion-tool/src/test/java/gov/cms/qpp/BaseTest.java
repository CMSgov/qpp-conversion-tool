package gov.cms.qpp;

import gov.cms.qpp.conversion.Converter;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 * handle generic test operations
 */
public class BaseTest {

	/**
	 * Runs before each test.
	 *
	 * Ensure empty scope before each test.
	 */
	@Before
	public void preCleanup() {
		resetScope();
		resetHistorical();
	}

	/**
	 * Does clean-up after an entire test suite.
	 *
	 * Ensures an empty scope after each test suite so different scopes do not leak into another test suite.
	 */
	@AfterClass
	public static void postSuiteCleanup() {
		resetScope();
		resetHistorical();
	}

	/**
	 * Retrieve fixture file content using "src/test/resources/fixtures/" as a base directory
	 *
	 * @param name file name
	 * @return file content
	 * @throws IOException when it can't locate / read the file
	 */
	protected static String getFixture(final String name) throws IOException {
		Path path = Paths.get("src/test/resources/fixtures/" + name);
		return new String(Files.readAllBytes(path));
	}

	/**
	 * Sets the scope to be empty.
	 */
	private static void resetScope() {
		Converter.setScope(new HashSet<>());
	}

	/**
	 * Sets the conversion to not be historical.
	 */
	private static void resetHistorical() {
		Converter.isHistorical(false);
	}
}
