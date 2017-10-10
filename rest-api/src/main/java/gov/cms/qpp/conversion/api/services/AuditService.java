package gov.cms.qpp.conversion.api.services;


import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface AuditService {
	CompletableFuture<Void> audit(InputStream fileContent, InputStream qppContent);
}
