package gov.cms.qpp.conversion.model.error;

import gov.cms.qpp.conversion.ConversionReport;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An {@link Exception} that is thrown from the {@link gov.cms.qpp.conversion.Converter} on error.
 */
public class TransformException extends RuntimeException {
	protected final transient ConversionReport conversionReport;

	/**
	 * Construct a new {@code TransformException} exception.
	 *
	 * @param message The detail message
	 * @param cause   A Throwable that caused this exception to occur.
	 * @param report  A report on the detail of the conversion.
	 */
	public TransformException(String message, Throwable cause, ConversionReport report) {
		super(message, cause);
		this.conversionReport = report;
	}

	/**
	 * Exposing the internal ConversionReport is intentional.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	public ConversionReport getConversionReport() {
		return conversionReport;
	}

	/**
	 * Exposing the internal AllErrors is intentional.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	public AllErrors getDetails() {
		return conversionReport.getReportDetails();
	}
}
