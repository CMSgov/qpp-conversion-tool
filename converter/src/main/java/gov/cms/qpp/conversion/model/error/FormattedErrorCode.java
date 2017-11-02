package gov.cms.qpp.conversion.model.error;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class FormattedErrorCode implements LocalizedError {

	private final ErrorCode errorCode;
	private final String message;

	public FormattedErrorCode(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public ErrorCode getErrorCode() {
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

		if (o instanceof FormattedErrorCode) {
			FormattedErrorCode that = (FormattedErrorCode) o;
			return that.getErrorCode() == errorCode && Objects.equals(that.message, message);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(errorCode, message);
	}

}
