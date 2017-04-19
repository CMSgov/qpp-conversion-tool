package gov.cms.qpp.conversion.model;

/**
 * Holds the error message from a Validator.
 * 
 * @author Scott Fradkin
 *
 */
public class ValidationError {

	private String errorText;
	private String xPath = "";

	public ValidationError(String text) {
		this.errorText = text;
	}

	public ValidationError(String text, String xPath) {
		this.errorText = text;
		this.xPath = xPath;
	}

	public String getErrorText() {
		return errorText;
	}

	public String getXPath() {
		return xPath;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ValidationError{");
		sb.append("errorText='").append(errorText).append('\'');
		sb.append(", xPath='").append(xPath).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
