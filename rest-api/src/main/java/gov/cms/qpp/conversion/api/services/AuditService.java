package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter.ConversionReport;

import gov.cms.qpp.conversion.api.model.Metadata;
import java.util.concurrent.CompletableFuture;

/**
 * Service Interface for auditing {@link Metadata} by {@link ConversionReport}
 */
public interface AuditService {
	/**
	 * Report a successful conversion.
	 *
	 * @param report {@link ConversionReport} that details the outcome of a conversion
	 * @return {@link CompletableFuture} that mules the result of report persistence
	 */
	CompletableFuture<Void> success(ConversionReport report);

	/**
	 * Report a failed conversion.
	 *
	 * @param report {@link ConversionReport} that details the outcome of a conversion
	 * @return {@link CompletableFuture} that mules the result of report persistence
	 */
	CompletableFuture<Void> failConversion(ConversionReport report);

	/**
	 * Report a failed QPP validation
	 *
	 * @param report {@link ConversionReport} that details the outcome of a conversion
	 * @return {@link CompletableFuture} that mules the result of report persistence
	 */
	CompletableFuture<Void> failValidation(ConversionReport report);
}
