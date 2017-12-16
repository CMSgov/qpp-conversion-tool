package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import java.io.IOException;
import java.util.List;

/**
 * Service interface to handle processing cpc+ files
 */
public interface CpcFileService {
	/**
	 * Retrieves all unprocessed cpc+ metadata
	 *
	 * @return {@link Metadata} extracted as {@link UnprocessedCpcFileData}.
	 */
	List<UnprocessedCpcFileData> getUnprocessedCpcPlusFiles();

	/**
	 * Retrieves the file location id by metadata id and uses it to retrieve the file
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return file contents parsed as a {@link String}
	 * @throws IOException for invalid IOUtils usage
	 */
	String getFileById(String fileId) throws IOException;
}
