package gov.cms.qpp.conversion.api.exceptions;


import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;

public class QppValidationException extends TransformException {

	/**
	 * Construct a new {@code QppValidationException} exception.
	 *
	 * @param message The detail message
	 * @param details The {@link AllErrors} that detail what went wrong.
	 */
	public QppValidationException(String message, AllErrors details) {
		super(message, null, details);
	}
}
