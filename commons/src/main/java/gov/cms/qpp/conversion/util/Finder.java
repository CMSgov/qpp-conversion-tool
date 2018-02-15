package gov.cms.qpp.conversion.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Finder extends SimpleFileVisitor<Path> {

	private final PathMatcher matcher;
	private final boolean recursive;
	private final Set<Path> files = new HashSet<>();

	public Finder(PathMatcher matcher, boolean recursive) {
		this.matcher = matcher;
		this.recursive = recursive;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
		find(file);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) {
		if (recursive) {
			try {
				Files.walkFileTree(directory, this);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		return FileVisitResult.CONTINUE;
	}

	private void find(Path path) {
		Path name = path.getFileName();
		if (name != null && matcher.matches(name)) {
			files.add(path);
		}
	}

	public List<Path> getFoundFiles() {
		return new ArrayList<>(files);
	}

}
