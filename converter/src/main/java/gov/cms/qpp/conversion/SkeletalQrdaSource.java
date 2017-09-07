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
		String filename = path.getFileName() != null ? path.getFileName().toString() : "";

		this.name = filename;
	}

	@Override
	public final String getName() {
		return name;
	}

}
