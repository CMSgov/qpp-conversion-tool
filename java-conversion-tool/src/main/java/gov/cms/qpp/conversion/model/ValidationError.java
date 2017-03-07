package gov.cms.qpp.conversion.model;

/**
 * Holds the error message from a Validator.
 * 
 * @author Scott Fradkin
 *
 */
public class ValidationError {

	private String errorText;

	public ValidationError(String text) {
		this.errorText = text;
	}

	public String getErrorText() {
		return errorText;
	}

}
