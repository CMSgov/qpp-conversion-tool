package gov.cms.qpp.test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class FileTestHelper {

	public static FileSystem createMockFileSystem() {
		FileSystem mock = Jimfs.newFileSystem(Configuration.unix());

		String qrdaFiles = "../qrda-files";
		Path source = Paths.get(qrdaFiles);
		Path destination = mock.getPath(qrdaFiles);

		try {
			Files.walkFileTree(source, new CopyFileVisitor(destination));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		return mock;
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