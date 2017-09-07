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
		String filename = isPathFileNameNull(path) ? path.getFileName().toString() : "";
		Objects.requireNonNull(filename, "name");

		this.name = filename;
	}

	private boolean isPathFileNameNull(Path path) {
		return !Objects.isNull(path) && !Objects.isNull(path.getFileName();
	}

	@Override
	public final String getName() {
		return name;
	}

}
