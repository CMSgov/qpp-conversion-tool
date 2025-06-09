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

	public void setError(Error theError) {
		error = theError;
	}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	public Error getError() {
		return error;
	}
}
