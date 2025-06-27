package gov.cms.qpp.conversion.api.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.cms.qpp.conversion.model.error.Error;

/**
 * A wrapper that holds a single {@link Error}.
 *
 * Returned from the validation API.
 */
public class ErrorMessage {
	private Error error;

	/**
	 * Storing the Error reference directly is intentional: the Error object is treated as immutable
	 * within our API model.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public void setError(Error theError) {
		this.error = theError;
	}

	/**
	 * @return the wrapped Error instance
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	public Error getError() {
		return error;
	}
}
