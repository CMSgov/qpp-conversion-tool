package gov.cms.qpp.conversion;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.base.Supplier;

class InputStreamSupplierSourceTest extends SourceTestSuite {

	private static InputStreamSupplierSource source(String path) throws IOException {
		return new InputStreamSupplierSource(path, stream(path));
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
		String text = "mock";
		InputStreamSupplierSource source = new InputStreamSupplierSource("DogCow name", new ByteArrayInputStream(text.getBytes()));

		assertThat(source.getSize()).isEqualTo(text.length());
	}

	@Test
	void testUnspecifiedSize() {
		byte [] bytes = "Moof".getBytes();
		InputStreamSupplierSource source = new InputStreamSupplierSource("DogCow name", new ByteArrayInputStream(bytes));

		assertThat(source.getSize()).isEqualTo(bytes.length);
	}
}
