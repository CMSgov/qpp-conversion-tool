package gov.cms.qpp.conversion.api.services;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface FileRetrievalService {
	CompletableFuture<InputStream> getFileById(String fileId);
}
