package gov.cms.qpp.conversion.model.error;

public class TransformException extends RuntimeException {
	private final AllErrors details;

	public TransformException(String message, Throwable cause, AllErrors details) {
		super(message, cause);
		this.details = details;
	}

	public AllErrors getDetails() {
		return details;
	}
}
