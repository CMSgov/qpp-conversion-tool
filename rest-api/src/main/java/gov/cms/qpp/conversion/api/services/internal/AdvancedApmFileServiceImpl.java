package gov.cms.qpp.conversion.api.services.internal;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import gov.cms.qpp.conversion.api.exceptions.InvalidFileTypeException;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.helper.AdvancedApmHelper;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;
import gov.cms.qpp.conversion.api.services.AdvancedApmFileService;
import gov.cms.qpp.conversion.api.services.DbService;
import gov.cms.qpp.conversion.api.services.StorageService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@Service
public class AdvancedApmFileServiceImpl implements AdvancedApmFileService {

	private DbService dbService;
	private StorageService storageService;

	/**
	 * Spring injects thread-safe service beans; suppress EI_EXPOSE_REP2 since sharing is intentional.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public AdvancedApmFileServiceImpl(DbService dbService, StorageService storageService) {
		this.dbService = dbService;
		this.storageService = storageService;
	}

	@Override
	public List<UnprocessedFileData> getUnprocessedPcfFiles(String orgAttribute) {
		List<Metadata> metadata = dbService.getUnprocessedPcfMetaData(orgAttribute);
		return AdvancedApmHelper.transformMetaDataToUnprocessedFileData(metadata);
	}

	@Override
	public InputStreamResource getPcfFileById(String fileId) {
		Metadata metadata = getMetadataById(fileId);
		if (AdvancedApmHelper.isAValidUnprocessedFile(metadata)) {
			return new InputStreamResource(storageService.getFileByLocationId(metadata.getSubmissionLocator()));
		}
		throw new NoFileInDatabaseException(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Override
	public InputStreamResource getQppById(String fileId) {
		Metadata metadata = getMetadataById(fileId);
		return new InputStreamResource(storageService.getFileByLocationId(metadata.getQppLocator()));
	}

	@Override
	public String updateFileStatus(final String fileId, final String org, final FileStatusUpdateRequest request) {
		String message;
		if (request != null && request.getProcessed() != null && !request.getProcessed()) {
			message = unprocessFileById(fileId, org);
		}
		else {
			message = processFileById(fileId, org);
		}
		return message;
	}

	private String processFileById(String fileId, String orgName) {
		Metadata metadata = getMetadataById(fileId);
		if(Constants.CPC_ORG.equalsIgnoreCase(orgName)) {
			metadata.setCpcProcessed(true);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return AdvancedApmHelper.FILE_FOUND_PROCESSED;
		} else if (Constants.RTI_ORG.equalsIgnoreCase(orgName)) {
			metadata.setRtiProcessed(true);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return AdvancedApmHelper.FILE_FOUND_PROCESSED;
		} else {
			return AdvancedApmHelper.FILE_NOT_FOUND;
		}
	}

	private String unprocessFileById(String fileId, String orgName) {
		Metadata metadata = getMetadataById(fileId);
		if (Constants.CPC_ORG.equalsIgnoreCase(orgName)) {
			metadata.setCpcProcessed(false);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return AdvancedApmHelper.FILE_FOUND_UNPROCESSED;
		} else if (Constants.RTI_ORG.equalsIgnoreCase(orgName)) {
			metadata.setRtiProcessed(false);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return AdvancedApmHelper.FILE_FOUND_UNPROCESSED;
		} else {
			return AdvancedApmHelper.FILE_NOT_FOUND;
		}
	}

	@Override
	public Metadata getMetadataById(String fileId) {
		Metadata metadata = dbService.getMetadataById(fileId);
		if (metadata == null) {
			throw new NoFileInDatabaseException(AdvancedApmHelper.FILE_NOT_FOUND);
		} else if (!AdvancedApmHelper.isPcfFile(metadata)) {
			throw new InvalidFileTypeException(AdvancedApmHelper.INVALID_FILE);
		}
		return metadata;
	}
}
