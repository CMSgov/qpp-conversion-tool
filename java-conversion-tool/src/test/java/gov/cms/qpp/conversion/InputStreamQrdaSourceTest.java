package gov.cms.qpp.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class InputStreamQrdaSourceTest extends QrdaSourceTestSuite {

	private static InputStreamQrdaSource source(String path) {
		return new InputStreamQrdaSource(path, stream(path));
	}

	private static InputStream stream(String path) {
		try {
			return Files.newInputStream(Paths.get(path));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public InputStreamQrdaSourceTest() {
		super("src/test/resources/arbitrary.txt", source("src/test/resources/arbitrary.txt"));
	}

	@Test
	public void testInputStream() throws IOException {
		String actual = IOUtils.toString(stream("src/test/resources/arbitrary.txt"), StandardCharsets.UTF_8);
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		Assert.assertEquals(actual, content);
	}

}
