package gov.cms.qpp.conversion.model;

public class ConstructorNotFoundException extends RuntimeException {

	public ConstructorNotFoundException() {}

	public ConstructorNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConstructorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstructorNotFoundException(String message) {
		super(message);
	}

	public ConstructorNotFoundException(Throwable cause) {
		super(cause);
	}
}
