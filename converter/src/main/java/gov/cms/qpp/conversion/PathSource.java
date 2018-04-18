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
		super(fileName(path));
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

	/**
	 * Returns null
	 */
	@Override
	public String getPurpose() {
		return null;
	}

	private static final String fileName(Path path) {
		if (path != null) {
			Path fileName = path.getFileName();
			if (fileName != null) {
				return fileName.toString();
			}
		}
		return "";
	}
}
