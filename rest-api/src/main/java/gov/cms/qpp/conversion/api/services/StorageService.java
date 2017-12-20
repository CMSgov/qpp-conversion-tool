package gov.cms.qpp.conversion.api.services;


import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import jdk.internal.util.xml.impl.Input;

/**
 * Interface to store an {@link InputStream} in S3.
 */
public interface StorageService {

	/**
	 * Stores the {@link InputStream} as an object in the S3 bucket.
	 *
	 * @param keyName The requested key name for the object.
	 * @param inStream The {@link InputStream} to write out to an object in S3.
	 * @return A {@link CompletableFuture} that will eventually contain the S3 object key.
	 */
	CompletableFuture<String> store(String keyName, InputStream inStream);

	/**
	 * Retrieve a CPC+ file by file id
	 *
	 * @param fileId Id of the file
	 * @return file to be returned
	 */
	InputStream getFileByLocationId(String fileId);
}
