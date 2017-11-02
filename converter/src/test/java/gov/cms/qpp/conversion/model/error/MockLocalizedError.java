package gov.cms.qpp.conversion.model.error;

public class MockLocalizedError implements LocalizedError {

	private final ErrorCode errorCode;
	private final String message;

	public MockLocalizedError(String message) {
		this(ErrorCode.UNEXPECTED_ERROR, message);
	}

	public MockLocalizedError(ErrorCode errorCode, String message) {
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

}
