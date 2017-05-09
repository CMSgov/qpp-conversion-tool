package gov.cms.qpp.conversion;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * handle generic test operations
 */
public class BaseTest {

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
	 * Retrieve fixture file content using "src/test/resources/fixtures/" as a base directory
	 *
	 * @param name file name
	 * @return stream of file content
	 * @throws IOException when it can't locate / read the file
	 */
	protected static InputStream getFixtureStream(final String name) throws IOException {
		Path path = Paths.get("src/test/resources/fixtures/" + name);
		return new BufferedInputStream(Files.newInputStream(path));
	}

}
