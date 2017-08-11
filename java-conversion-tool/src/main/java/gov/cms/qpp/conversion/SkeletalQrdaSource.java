package gov.cms.qpp.conversion;

import java.util.Objects;

public abstract class SkeletalQrdaSource implements QrdaSource {

	private final String name;

	public SkeletalQrdaSource(String name) {
		Objects.requireNonNull(name, "name");

		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

}
