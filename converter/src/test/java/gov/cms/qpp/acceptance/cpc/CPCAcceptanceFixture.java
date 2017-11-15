package gov.cms.qpp.acceptance.cpc;

import java.util.List;

public class CPCAcceptanceFixture {
	private boolean strict;
	private List<FixtureErrorData> errorData;

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public List<FixtureErrorData> getErrorData() {
		return errorData;
	}

	public void setErrorData(List<FixtureErrorData> errorData) {
		this.errorData = errorData;
	}
}
