package gov.cms.qpp.conversion.model.error;


import gov.cms.qpp.conversion.Converter;

public class QppValidationException extends TransformException {

	/**
	 * Construct a new {@code QppValidationException} exception.
	 *
	 * @param message The detail message
	 * @param cause A Throwable that caused this exception to occur.
	 * @param report A report on the detail of the conversion.
	 */
	public QppValidationException(String message, Throwable cause, Converter.ConversionReport report) {
		super(message, cause, report);
	}

}
