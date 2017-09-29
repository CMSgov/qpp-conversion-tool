package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.api.model.Metadata;

import java.util.concurrent.CompletableFuture;

public interface DbService {
	CompletableFuture<Metadata> write(Metadata meta);
}
