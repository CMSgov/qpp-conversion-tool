package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.api.model.Metadata;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for reading/writing a {@link Metadata} object to a database.
 */
public interface DbService {
	/**
	 * Writes the passed in {@link Metadata} to the database.
	 *
	 * @param meta The metadata to write.
	 * @return A {@link CompletableFuture} that will hold the written Metadata.
	 */
	CompletableFuture<Metadata> write(Metadata meta);

	List<Metadata> getUnprocessedCpcPlusMetaData();

	/**
	 * Retrieves the FileLocationId from the database by Metadata id
	 *
	 * @param uuid Id of the Metadata holding the FileLocationId
	 * @return File Location Id
	 */
	String getFileSubmissionLocationId(String uuid);
}
