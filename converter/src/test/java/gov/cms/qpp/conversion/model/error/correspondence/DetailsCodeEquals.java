package gov.cms.qpp.conversion.model.error.correspondence;

import com.google.common.truth.Correspondence;

import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;

public final class DetailsCodeEquals implements Correspondence.BinaryPredicate<Detail, ProblemCode> {

	public static Correspondence<Detail, ProblemCode> INSTANCE = Correspondence.from(new DetailsCodeEquals(), "Compare a Detail and an ProblemCode");

	@Override
	public boolean apply(Detail actual, ProblemCode expected) {
		if (actual == null) {
			return expected == null;
		}
		ProblemCode error = ProblemCode.getByCode(actual.getErrorCode());
		return error == expected;
	}

	@Override
	public String toString() {
		return null;
	}
}
