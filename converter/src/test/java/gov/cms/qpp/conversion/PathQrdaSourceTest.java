package gov.cms.qpp.conversion;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathQrdaSourceTest extends QrdaSourceTestSuite {

	public PathQrdaSourceTest() {
		super("arbitrary.txt", new PathQrdaSource(Paths.get("src/test/resources/arbitrary.txt")));
	}

	@Test
	public void testInputStream() throws IOException {
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		Assert.assertEquals("hello, world", content);
	}

	@Test
	public void testNullPath() {
		PathQrdaSource testSource = new PathQrdaSource(null);
		assertThat(testSource.getName(), isEmptyString());
	}

	@Test
	public void testNullFileName() {
		Path mockPath = mock(Path.class);
		when(mockPath.getFileName()).thenReturn(null);
		PathQrdaSource testSource = new PathQrdaSource(mockPath);
		assertThat(testSource.getName(), isEmptyString());
	}

}