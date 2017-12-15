package gov.cms.qpp.conversion;

import java.util.Objects;

public abstract class SkeletalSource implements Source {

	private final String name;

	public SkeletalSource(String name) {
		Objects.requireNonNull(name, "name");

		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}
}
