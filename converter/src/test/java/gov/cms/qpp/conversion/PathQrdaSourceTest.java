package gov.cms.qpp.conversion;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathQrdaSourceTest extends QrdaSourceTestSuite {

	public PathQrdaSourceTest() {
		super("arbitrary.txt", new PathQrdaSource(Paths.get("src/test/resources/arbitrary.txt")));
	}

	@Test
	public void testInputStream() throws IOException {
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		assertWithMessage("stream content was not as expected")
				.that(content).isEqualTo("hello, world");
	}

	@Test
	public void testNullPath() {
		PathQrdaSource testSource = new PathQrdaSource(null);
		assertWithMessage("name should be empty")
				.that(testSource.getName()).isEmpty();
	}

	@Test
	public void testNullFileName() {
		Path mockPath = mock(Path.class);
		when(mockPath.getFileName()).thenReturn(null);
		PathQrdaSource testSource = new PathQrdaSource(mockPath);
		assertWithMessage("name should be empty")
				.that(testSource.getName()).isEmpty();
	}

}