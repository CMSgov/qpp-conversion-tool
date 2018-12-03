package gov.cms.qpp.acceptance.cpc;

import java.util.List;


/**
 * Test fixture meant to encapsulate CPC+ acceptance criteria.
 */
public class CPCAcceptanceFixture {
	private boolean strict;
	private List<FixtureErrorData> errorData;

	/**
	 * Should engage in bi-directional validation. i.e. verify that all conversion
	 * errors are represented by the file's fixtures and all error occurrences mentioned
	 * in the file's fixtures are found in the conversion's error output.
	 * @return
	 */
	boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * Collection of fixture error details.
	 *
	 * @return collection of errors and their accumulations
	 */
	List<FixtureErrorData> getErrorData() {
		return errorData;
	}

	public void setErrorData(List<FixtureErrorData> errorData) {
		this.errorData = errorData;
	}
}
