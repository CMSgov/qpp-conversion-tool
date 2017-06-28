package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.encode.JsonWrapper;

/**
 * Interface for the QPP Validation Service
 */
public interface ValidationService {
	/**
	 * Validates that the given QPP is valid.
	 *
	 * @param qpp The QPP input.
	 */
	public void validateQpp(JsonWrapper qpp);
}
