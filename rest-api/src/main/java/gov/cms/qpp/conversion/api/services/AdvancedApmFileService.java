package gov.cms.qpp.conversion.api.services;

import org.springframework.core.io.InputStreamResource;

import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;

import java.io.IOException;
import java.util.List;

public interface AdvancedApmFileService {

	/**
	 * Retrieves all unprocessed pcf metadata
	 *
	 * @return {@link Metadata} extracted as {@link UnprocessedFileData}.
	 */
	List<UnprocessedFileData> getUnprocessedPcfFiles(String org);

	/**
	 * Retrieves the file location id by metadata id and uses it to retrieve the file
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return file contents parsed as a {@link String}
	 * @throws IOException for invalid IOUtils usage
	 */
	InputStreamResource getPcfFileById(String fileId) throws IOException;

	/**
	 * Retrieves all unprocessed cpc+ metadata
	 *
	 * @return {@link Metadata} extracted as {@link UnprocessedFileData}.
	 */
	List<UnprocessedFileData> getUnprocessedCpcPlusFiles(String orgAttribute);

	/**
	 * Retrieves the file location id by metadata id and uses it to retrieve the file
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return file contents parsed as a {@link String}
	 * @throws IOException for invalid IOUtils usage
	 */
	InputStreamResource getCpcFileById(String fileId) throws IOException;

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
