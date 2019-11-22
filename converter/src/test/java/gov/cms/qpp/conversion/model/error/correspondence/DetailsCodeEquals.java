package gov.cms.qpp.conversion.model.error.correspondence;

import com.google.common.truth.Correspondence;

import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;

public final class DetailsCodeEquals implements Correspondence.BinaryPredicate<Detail, ErrorCode> {

	public static Correspondence<Detail, ErrorCode> INSTANCE = Correspondence.from(new DetailsCodeEquals(), "Compare a Detail and an ErrorCode");

	@Override
	public boolean apply(Detail actual, ErrorCode expected) {
		if (actual == null) {
			return expected == null;
		}
		ErrorCode error = ErrorCode.getByCode(actual.getErrorCode());
		return error == expected;
	}

	@Override
	public String toString() {
		return null;
	}
}
