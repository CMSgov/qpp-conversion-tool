package gov.cms.qpp.conversion.api.services;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for CPC+ File retrieval
 */
public interface FileRetrievalService {
	/**
	 * Retrieve a CPC+ file by file id
	 *
	 * @param fileId Id of the file
	 * @return file to be returned
	 */
	CompletableFuture<InputStream> getFileById(String fileId);
}
