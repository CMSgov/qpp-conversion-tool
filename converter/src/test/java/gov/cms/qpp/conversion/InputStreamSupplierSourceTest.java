package gov.cms.qpp.conversion;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

class InputStreamSupplierSourceTest extends SourceTestSuite {

	private static InputStreamSupplierSource source(String path) throws IOException {
		return new InputStreamSupplierSource(path, () -> stream(path), Files.size(Paths.get(path)));
	}

	private static InputStream stream(String path) {
		try {
			return Files.newInputStream(Paths.get(path));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	InputStreamSupplierSourceTest() throws IOException {
		super("src/test/resources/arbitrary.txt", source("src/test/resources/arbitrary.txt"));
	}

	@Test
	void testInputStream() throws IOException {
		String actual = IOUtils.toString(stream("src/test/resources/arbitrary.txt"), StandardCharsets.UTF_8);
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		assertThat(actual).isEqualTo(content);
	}

	@Test
	void testSpecificSize() {
		long size = 26;
		InputStreamSupplierSource source = new InputStreamSupplierSource("DogCow name", () -> new ByteArrayInputStream("Moof".getBytes()), size);

		assertThat(source.getSize()).isEqualTo(size);
	}

	@Test
	void testUnspecifiedSize() {
		byte [] bytes = "Moof".getBytes();
		InputStreamSupplierSource source = new InputStreamSupplierSource("DogCow name", () -> new ByteArrayInputStream(bytes));

		assertThat(source.getSize()).isEqualTo(bytes.length);
	}
}
