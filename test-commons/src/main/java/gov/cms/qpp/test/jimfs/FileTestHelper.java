package gov.cms.qpp.test.jimfs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import gov.cms.qpp.test.CopyFileVisitor;

public class FileTestHelper {

	public static FileSystem createMockFileSystem(Configuration configuration) {
		FileSystem mock = Jimfs.newFileSystem();

		copy("../qrda-files", mock);
		copy("src/test/resources", mock);

		return mock;
	}

	private static void copy(String path, FileSystem mock) {
		Path source = Paths.get(path);
		Path destination = mock.getPath(path);

		try {
			Files.walkFileTree(source, new CopyFileVisitor(destination));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static Stream<Path> getAllQrdaFiles(FileSystem fileSystem, String extension) {
		try {
			return Files.walk(fileSystem.getPath("../qrda-files"))
					.filter(Files::isRegularFile)
					.map(Path::toAbsolutePath)
					.filter(path -> path.toString().endsWith(extension));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private FileTestHelper() {
	}

}