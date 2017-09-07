package gov.cms.qpp.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathQrdaSource extends SkeletalQrdaSource {

	private final Path path;

	public PathQrdaSource(Path path) {
		super(path);
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

}