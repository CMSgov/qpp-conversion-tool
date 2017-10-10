package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class AuditServiceImpl implements AuditService{

	@Autowired
	private StorageService storageService;

	@Autowired
	private DbService dbService;

	@Autowired
	private Metadata metadata;

	@Override
	public void audit(InputStream fileContent, InputStream qppContent) {
		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(fileContent).thenAccept(metadata::setSubmissionLocator),
				storeContent(qppContent).thenAccept(metadata::setQppLocator));
		allWrites.whenComplete(this::persist);
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
