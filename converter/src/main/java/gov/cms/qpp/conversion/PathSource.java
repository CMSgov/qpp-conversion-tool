package gov.cms.qpp.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathSource extends SkeletalSource {

	private final Path path;

	public PathSource(Path path) {
		super(path != null && path.getFileName() != null ? path.getFileName().toString() : "");
		this.path = path;
	}

	@Override
	public InputStream toInputStream() {
		try {
			return Files.newInputStream(path);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	@Override
	public long getSize() {
		try {
			return Files.size(path);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}
}
