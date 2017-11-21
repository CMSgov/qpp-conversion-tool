package gov.cms.qpp.acceptance.cpc;

import gov.cms.qpp.conversion.model.error.ErrorCode;

import java.util.List;

/**
 * CPC+ fixture data meant to describe the prevalence of errors
 * in a conversion's output.
 */
public class FixtureErrorData {
	private int errorCode;
	private int occurrences;
	private String message;
	private List<Object> subs;

	/**
	 * Index value for a {@link ErrorCode} that pertains to this fixture.
	 *
	 * @return error code
	 */
	int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * The amount of occurrences of the error in the conversion output.
	 *
	 * @return occurrence count
	 */
	int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}

	/**
	 * A collection of substitution values to be used in formatting the
	 * {@link ErrorCode}'s message.
	 *
	 * @return collection of substitution values
	 */
	List<Object> getSubs() {
		return subs;
	}

	public void setSubs(List<Object> subs) {
		this.subs = subs;
	}

	/**
	 * Get or generate an {@link ErrorCode}'s message.
	 *
	 * @return generated error message
	 */
	public String getMessage() {
		if (message == null) {
			ErrorCode ec = ErrorCode.getByCode(errorCode);
			message = (subs != null) ? ec.format(subs.toArray()).getMessage() : ec.getMessage();
		}
		return message;
	}
}
