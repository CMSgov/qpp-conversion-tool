package gov.cms.qpp.conversion.model.error.correspondence;

import java.util.Objects;

import com.google.common.truth.Correspondence;

import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;

public final class DetailsErrorEquals implements Correspondence.BinaryPredicate<Detail, LocalizedProblem> {

	public static Correspondence<Detail, LocalizedProblem> INSTANCE = Correspondence.from(new DetailsErrorEquals(), "Compare a Detail and a LocalizedProblem");

	@Override
	public boolean apply(Detail actual, LocalizedProblem expected) {
		if (actual == null) {
			return expected == null;
		}
		ProblemCode error = actual.getErrorCode() == null ? null : ProblemCode.getByCode(actual.getErrorCode());



		return Objects.equals(actual.getMessage(), expected.getMessage()) &&
				error == expected.getProblemCode();
	}

	@Override
	public String toString() {
		return null;
	}
}
