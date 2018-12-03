package gov.cms.qpp.test.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NioHelper {

	/**
	 * Returns an InputStream sourced by the given path.
	 *
	 * @param file An XML file.
	 * @return InputStream for the file's content
	 */
	public static InputStream fileToStream(Path file) {
		try {
			return Files.newInputStream(file);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private NioHelper() {
	}

}
