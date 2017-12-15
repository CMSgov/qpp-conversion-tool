package gov.cms.qpp.conversion;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

class InputStreamSourceTest extends SourceTestSuite {

	private static InputStreamSupplierSource source(String path) {
		return new InputStreamSupplierSource(path, () -> stream(path));
	}

	private static InputStream stream(String path) {
		try {
			return Files.newInputStream(Paths.get(path));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	InputStreamSourceTest() {
		super("src/test/resources/arbitrary.txt", source("src/test/resources/arbitrary.txt"));
	}

	@Test
	void testInputStream() throws IOException {
		String actual = IOUtils.toString(stream("src/test/resources/arbitrary.txt"), StandardCharsets.UTF_8);
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		assertThat(actual).isEqualTo(content);
	}

}
