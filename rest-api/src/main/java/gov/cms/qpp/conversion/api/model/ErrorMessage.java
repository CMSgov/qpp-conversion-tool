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
	 * Stores a defensive reference to the provided Error.
	 */
	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "No copy constructor available; trusting caller to not modify after setting"
	)
	public void setError(Error theError) {
		error = theError;
	}

	/**
	 * Returns the internal Error instance.
	 */
	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP",
			justification = "No defensive copy available; consumers should treat returned Error as read-only"
	)
	public Error getError() {
		return error;
	}
}
