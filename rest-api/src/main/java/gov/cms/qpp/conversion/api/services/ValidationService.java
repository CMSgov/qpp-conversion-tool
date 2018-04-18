package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.ConversionReport;

/**
 * Interface for the QPP Validation Service
 */
public interface ValidationService {
	/**
	 * Validates that the given QPP is valid.
	 *
	 * @param conversionReport A report on the state of the conversion.
	 */
	void validateQpp(ConversionReport conversionReport);
}
