package gov.cms.qpp.conversion;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class PathQrdaSourceTest extends QrdaSourceTestSuite {

	PathQrdaSourceTest() {
		super("arbitrary.txt", new PathQrdaSource(Paths.get("src/test/resources/arbitrary.txt")));
	}

	@Test
	void testInputStream() throws IOException {
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		assertWithMessage("stream content was not as expected")
				.that(content).isEqualTo("hello, world");
	}

	@Test
	void testNullPath() {
		PathQrdaSource testSource = new PathQrdaSource(null);
		assertWithMessage("name should be empty")
				.that(testSource.getName()).isEmpty();
	}

	@Test
	void testNullFileName() {
		Path mockPath = mock(Path.class);
		when(mockPath.getFileName()).thenReturn(null);
		PathQrdaSource testSource = new PathQrdaSource(mockPath);
		assertWithMessage("name should be empty")
				.that(testSource.getName()).isEmpty();
	}

}