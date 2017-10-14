package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter.ConversionReport;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface AuditService {
	CompletableFuture<Void> success(ConversionReport report);
	CompletableFuture<Void> failConversion(ConversionReport report);
	CompletableFuture<Void> failValidation(InputStream fileContent, InputStream qppContent);
}
