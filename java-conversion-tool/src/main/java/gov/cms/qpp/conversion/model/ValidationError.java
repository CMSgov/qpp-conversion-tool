package gov.cms.qpp.conversion.model;

public class ValidationError {

	private String errorText;

	public ValidationError(String text) {
		this.errorText = text;
	}

	public String getErrorText() {
		return errorText;
	}

}
