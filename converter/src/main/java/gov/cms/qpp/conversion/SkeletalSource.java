package gov.cms.qpp.conversion;

import java.util.Objects;

/**
 * An abstract class that implements just the name aspect of a source.
 */
public abstract class SkeletalSource implements Source {

	private final String name;

	/**
	 * Creates a new source with the given name.
	 * @param name The name of this new source.
	 */
	public SkeletalSource(String name) {
		Objects.requireNonNull(name, "name");

		this.name = name;
	}

	/**
	 * Returns the name.
	 *
	 * @return The name of the source.
	 */
	@Override
	public final String getName() {
		return name;
	}
}
