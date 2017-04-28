package gov.cms.qpp;

import gov.cms.qpp.conversion.ConversionEntry;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 * handle generic test operations
 */
public class BaseTest {

	/**
	 * Ensure empty scope before each test.
	 *
	 * @throws NoSuchFieldException if scope field can't be located
	 * @throws IllegalAccessException should scope not be accessible
	 */
	@Before
	public void preCleanup() throws NoSuchFieldException, IllegalAccessException {
		Field scope = ConversionEntry.class.getDeclaredField("scope");
		scope.setAccessible(true);
		scope.set(null, new HashSet<>());
	}

	/**
	 * Ensures an empty scope after each test suite so different scopes do not leak into another test suite.
	 *
	 * @throws NoSuchFieldException if scope field can't be located
	 * @throws IllegalAccessException should scope not be accessible
	 */
	@AfterClass
	public static void postSuiteCleanup() throws NoSuchFieldException, IllegalAccessException {
		Field scope = ConversionEntry.class.getDeclaredField("scope");
		scope.setAccessible(true);
		scope.set(null, new HashSet<>());
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
}
