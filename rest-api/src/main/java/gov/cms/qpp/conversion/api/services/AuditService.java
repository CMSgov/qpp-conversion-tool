package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface AuditService {
	CompletableFuture<Void> success(InputStream fileContent, Converter converter);
	CompletableFuture<Void> failConversion(InputStream fileContent, InputStream qppContent);
	CompletableFuture<Void> failValidation(InputStream fileContent, InputStream qppContent);
}
