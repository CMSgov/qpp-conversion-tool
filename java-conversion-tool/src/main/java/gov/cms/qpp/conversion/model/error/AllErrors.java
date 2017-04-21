package gov.cms.qpp.conversion.model.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of error errorSources.
 */
public class AllErrors {
	private List<ErrorSource> errorSources;

	/**
	 * Constructs an {@code AllErrors} with no errorSources.
	 */
	public AllErrors() {
		//empty on purpose
	}

	/**
	 * Constructs a {@code AllErrors} with the specified {@link gov.cms.qpp.conversion.model.error.ErrorSource}s.
	 *
	 * @param errorSources The list of {@code ErrorSource}s.
	 */
	public AllErrors(List<ErrorSource> errorSources) {
		this.errorSources = errorSources;
	}

	/**
	 * Gets all the {@link gov.cms.qpp.conversion.model.error.ErrorSource}s.
	 *
	 * @return All the {@code ErrorSource}s.
	 */
	public List<ErrorSource> getErrorSources() {
		return errorSources;
	}

	/**
	 * Sets all the {@link gov.cms.qpp.conversion.model.error.ErrorSource}.
	 *
	 * @param errorSources The {@code ErrorSource}s to use.
	 */
	public void setErrorSources(final List<ErrorSource> errorSources) {
		this.errorSources = errorSources;
	}

	/**
	 * Adds a {@link gov.cms.qpp.conversion.model.error.ErrorSource} to the list.
	 *
	 * @param source The {@code ErrorSource} to add.
	 */
	public void addErrorSource(ErrorSource source) {
		if (null == errorSources) {
			errorSources = new ArrayList<>();
		}

		errorSources.add(source);
	}
}
