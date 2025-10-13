package gov.cms.qpp.conversion.api.services;

import org.springframework.core.io.InputStreamResource;

import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;

import java.io.IOException;
import java.util.List;

public interface AdvancedApmFileService {

	/**
	 * Retrieves the file location id by metadata id and uses it to retrieve the submission's QPP
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return QPP contents parsed as a {@link String}
	 * @throws IOException for invalid IOUtils usage
	 */
	InputStreamResource getQppById(String fileId) throws IOException;

	/**
 	 * Updates the file status as processed or unprocessed.
	 *
	 * @param fileId
	 * @param org
	 * @param request
	 * @return
	 */
	String updateFileStatus(final String fileId, final String org, final FileStatusUpdateRequest request);

	/**
	 * Retrieve metadata based on file id
	 *
	 * @param fileId
	 * @return
	 */
	Metadata getMetadataById(String fileId);
}
