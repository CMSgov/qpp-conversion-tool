package gov.cms.qpp.test.jimfs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class CopyFileVisitor extends SimpleFileVisitor<Path> {

	private final Path destination;
	private Path source = null;

	public CopyFileVisitor(Path destination) {
		Objects.requireNonNull(destination, "destination");
		this.destination = destination;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
		if (source == null) {
			source = directory;
		}
		Files.createDirectories(toDestinationPath(directory));
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.copy(file, toDestinationPath(file));
		return FileVisitResult.CONTINUE;
	}

	private Path toDestinationPath(Path file) {
		return destination.resolve(source.relativize(file).toString()
				.replace(source.getFileSystem().getSeparator(), destination.getFileSystem().getSeparator()));
	}

}
