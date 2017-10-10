package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuditServiceImpl implements AuditService{

	@Autowired
	private StorageService storageService;

	@Autowired
	private DbService dbService;

	@Autowired
	private Metadata metadata;

	@Override
	public CompletableFuture<Void> audit(InputStream fileContent, InputStream qppContent) {
		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(fileContent).thenAccept(metadata::setSubmissionLocator),
				storeContent(qppContent).thenAccept(metadata::setQppLocator));
		return allWrites.whenComplete(this::persist);
	}

	CompletableFuture<String> storeContent(InputStream content) {
		UUID key = UUID.randomUUID();
		return storageService.store(key.toString(), content);
	}

	CompletableFuture<Metadata> persist(Void novalue, Throwable thrown) {
		if (thrown != null) {
			throw new AuditException(thrown);
		}
		return dbService.write(metadata);
	}
}
