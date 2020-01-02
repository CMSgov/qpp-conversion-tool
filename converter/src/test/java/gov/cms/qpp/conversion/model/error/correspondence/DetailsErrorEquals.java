package gov.cms.qpp.conversion.model.error.correspondence;

import java.util.Objects;

import com.google.common.truth.Correspondence;

import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;

public final class DetailsErrorEquals implements Correspondence.BinaryPredicate<Detail, LocalizedError> {

	public static Correspondence<Detail, LocalizedError> INSTANCE = Correspondence.from(new DetailsErrorEquals(), "Compare a Detail and a LocalizedError");

	@Override
	public boolean apply(Detail actual, LocalizedError expected) {
		if (actual == null) {
			return expected == null;
		}
		ErrorCode error = actual.getErrorCode() == null ? null : ErrorCode.getByCode(actual.getErrorCode());



		return Objects.equals(actual.getMessage(), expected.getMessage()) &&
				error == expected.getErrorCode();
	}

	@Override
	public String toString() {
		return null;
	}
}
