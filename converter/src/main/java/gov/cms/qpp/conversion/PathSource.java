package gov.cms.qpp.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link Source} represented by a path that points to a file in the file system.
 */
public class PathSource extends SkeletalSource {

	private final Path path;

	public PathSource(Path path) {
		super(path != null && path.getFileName() != null ? path.getFileName().toString() : "");
		this.path = path;
	}

	/**
	 * An {@link InputStream} representation of the file at the path.
	 *
	 * @return An InputStream representing the source.
	 */
	@Override
	public InputStream toInputStream() {
		try {
			return Files.newInputStream(path);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	/**
	 * The size of the file specified by the path.
	 *
	 * @return The source's size.
	 */
	@Override
	public long getSize() {
		try {
			return Files.size(path);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}
}
