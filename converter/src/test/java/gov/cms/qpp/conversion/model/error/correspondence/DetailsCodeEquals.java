package gov.cms.qpp.conversion.model.error.correspondence;

import com.google.common.truth.Correspondence;

import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;

public final class DetailsCodeEquals extends Correspondence<Detail, ErrorCode> {

	private DetailsCodeEquals() {
	}

	public static DetailsCodeEquals INSTANCE = new DetailsCodeEquals();

	@Override
	public boolean compare(Detail actual, ErrorCode expected) {
		if (actual == null) {
			return expected == null;
		}
		return actual.getErrorCode().getErrorCode() == expected;
	}

	@Override
	public String toString() {
		return null;
	}
}
