package gov.cms.qpp.conversion.api.services;

import org.springframework.core.io.InputStreamResource;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;

import java.io.IOException;
import java.util.List;

public interface PcfFileService {

	List<UnprocessedCpcFileData> getUnprocessedPcfPlusFiles();

	Metadata getMetadataById(String fileId);

	/**
	 * Retrieves the file location id by metadata id and uses it to retrieve the file
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return file contents parsed as a {@link String}
	 * @throws IOException for invalid IOUtils usage
	 */
	InputStreamResource getFileById(String fileId) throws IOException;

	/**
	 * Retrieves the file location id by metadata id and uses it to retrieve the submission's QPP
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return QPP contents parsed as a {@link String}
	 * @throws IOException for invalid IOUtils usage
	 */
	InputStreamResource getQppById(String fileId) throws IOException;

	/**
	 * Marks a CPC File as processed by id
	 *
	 * @param fileId Identifier of the PCF file
	 * @return Success or failure message
	 */
	String processFileById(String fileId, String orgName);

	/**
	 * Marks a CPC File as unprocessed by id
	 *
	 * @param fileId Identifier of the PCF file
	 * @return Success or failure message
	 */
	String unprocessFileById(String fileId, String orgName);
}
