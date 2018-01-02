package gov.cms.qpp.conversion;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.test.jimfs.JimfsContract;
import gov.cms.qpp.test.jimfs.JimfsTest;

class PathSourceTest extends SourceTestSuite implements JimfsContract {

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

	@JimfsTest
	void testInvalidPathInputStreamThrowsUncheckedIOException(FileSystem fileSystem) {
		Path path = fileSystem.getPath(UUID.randomUUID().toString());
		PathSource testSource = new PathSource(path);
		Assertions.assertThrows(UncheckedIOException.class, testSource::toInputStream);
	}

	@JimfsTest
	void testInvalidPathSizeThrowsUncheckedIOException(FileSystem fileSystem) {
		Path path = fileSystem.getPath(UUID.randomUUID().toString());
		PathSource testSource = new PathSource(path);
		Assertions.assertThrows(UncheckedIOException.class, testSource::getSize);
	}

	@Test
	void testSize() throws IOException {
		assertThat(source.getSize()).isEqualTo(IOUtils.toByteArray(source.toInputStream()).length);
	}
}
