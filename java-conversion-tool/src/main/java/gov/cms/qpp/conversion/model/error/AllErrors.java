package gov.cms.qpp.conversion.model.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of error sources.
 */
public class AllErrors {
	private List<ErrorSource> sources;

	/**
	 * Constructs an {@code AllErrors} with no sources.
	 */
	public AllErrors() {
		//empty on purpose
	}

	/**
	 * Constructs a {@code AllErrors} with the specified {@link gov.cms.qpp.conversion.model.error.ErrorSource}s.
	 *
	 * @param sources The list of {@code ErrorSource}s.
	 */
	public AllErrors(List<ErrorSource> sources) {
		this.sources = sources;
	}

	/**
	 * Gets all the {@link gov.cms.qpp.conversion.model.error.ErrorSource}s.
	 *
	 * @return All the {@code ErrorSource}s.
	 */
	public List<ErrorSource> getSources() {
		return sources;
	}

	/**
	 * Sets all the {@link gov.cms.qpp.conversion.model.error.ErrorSource}.
	 *
	 * @param sources The {@code ErrorSource}s to use.
	 */
	public void setSources(final List<ErrorSource> sources) {
		this.sources = sources;
	}

	/**
	 * Adds a {@link gov.cms.qpp.conversion.model.error.ErrorSource} to the list.
	 *
	 * @param source The {@code ErrorSource} to add.
	 */
	public void addErrorSource(ErrorSource source) {
		if (null == sources) {
			sources = new ArrayList<>();
		}

		sources.add(source);
	}
}
