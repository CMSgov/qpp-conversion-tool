package gov.cms.qpp.conversion;

import java.nio.file.Path;
import java.util.Objects;

public abstract class SkeletalQrdaSource implements QrdaSource {

	private final String name;

	public SkeletalQrdaSource(String name) {
		Objects.requireNonNull(name, "name");

		this.name = name;
	}

	public SkeletalQrdaSource(Path path) {
		String filename = isValidPath(path) ? path.getFileName().toString() : null;
		Objects.requireNonNull(filename, "name");

		this.name = filename;
	}

	private boolean isValidPath(Path path) {
		return path != null && path.getFileName() != null;
	}

	@Override
	public final String getName() {
		return name;
	}

}
