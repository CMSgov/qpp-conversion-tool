package gov.cms.qpp.conversion;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PathSourceTest extends SourceTestSuite {

	PathSourceTest() {
		super("arbitrary.txt", new PathSource(Paths.get("src/test/resources/arbitrary.txt")));
	}

	@Test
	void testInputStream() throws IOException {
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		assertWithMessage("stream content was not as expected")
				.that(content).isEqualTo("hello, world");
	}

	@Test
	void testNullPath() {
		PathSource testSource = new PathSource(null);
		assertWithMessage("name should be empty")
				.that(testSource.getName()).isEmpty();
	}

	@Test
	void testNullFileName() {
		Path mockPath = mock(Path.class);
		when(mockPath.getFileName()).thenReturn(null);
		PathSource testSource = new PathSource(mockPath);
		assertWithMessage("name should be empty")
				.that(testSource.getName()).isEmpty();
	}

	@Test
	void testSize() throws IOException {
		assertThat(source.getSize()).isEqualTo(IOUtils.toByteArray(source.toInputStream()).length);
	}
}
