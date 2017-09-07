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
		Path filename = path.getFileName();
		String name = "";
		if (filename != null) {
			name = filename.toString();
		}
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

}
