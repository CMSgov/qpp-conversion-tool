package gov.cms.qpp.conversion.model.error;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class FormattedProblemCode implements LocalizedProblem {

	private final ProblemCode errorCode;
	private final String message;

	public FormattedProblemCode(ProblemCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public ProblemCode getProblemCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("errorCode", errorCode)
				.add("message", message)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (o.getClass() == getClass()) {
			FormattedProblemCode that = (FormattedProblemCode) o;
			return that.errorCode == errorCode && Objects.equals(that.message, message);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(errorCode, message);
	}

}
