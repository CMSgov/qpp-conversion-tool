package gov.cms.qpp.conversion.api.services.internal;

import gov.cms.qpp.conversion.api.exceptions.InvalidFileTypeException;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import gov.cms.qpp.conversion.api.services.CpcFileService;
import gov.cms.qpp.conversion.api.services.DbService;
import gov.cms.qpp.conversion.api.services.StorageService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service for handling Cpc File meta data
 */
@Service
public class CpcFileServiceImpl implements CpcFileService {

	public static final String FILE_NOT_FOUND = "File not found!";
	protected static final String INVALID_FILE = "The file was not a CPC+ file.";
	protected static final String FILE_FOUND_PROCESSED = "The file was found and will be updated as processed.";
	protected static final String FILE_FOUND_UNPROCESSED = "The file was found and will be updated as unprocessed.";

	private DbService dbService;
	private StorageService storageService;

	/**
	 * initialize
	 *
	 * @param dbService service to persist conversion metadata
	 * @param storageService store conversion output
	 */
	public CpcFileServiceImpl(DbService dbService, StorageService storageService) {
		this.dbService = dbService;
		this.storageService = storageService;
	}

	/**
	 * Calls the DbService for unprocessed metadata to transform into UnprocessedCpcFileData
	 *
	 * @return List of {@link UnprocessedCpcFileData}
	 */
	@Override
	public List<UnprocessedCpcFileData> getUnprocessedCpcPlusFiles(String orgAttribute) {
		List<Metadata> metadata = dbService.getUnprocessedCpcPlusMetaData(orgAttribute);

		return transformMetaDataToUnprocessedCpcFileData(metadata);
	}

	/**
	 * Retrieves the file location id and retrieves the file if it is an unprocessed cpc+ file
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return file contents as a {@link String}
	 */
	@Override
	public InputStreamResource getFileById(String fileId) {
		Metadata metadata = getMetadataById(fileId);
		if (isAnUnprocessedCpcFile(metadata)) {
			return new InputStreamResource(storageService.getFileByLocationId(metadata.getSubmissionLocator()));
		}
		throw new NoFileInDatabaseException(FILE_NOT_FOUND);
	}

	/**
	 * Retrieves the file location id and retrieves the corresponding submission's QPP
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return QPP contents as a {@link String}
	 */
	@Override
	public InputStreamResource getQppById(String fileId) {
		Metadata metadata = getMetadataById(fileId);
		return new InputStreamResource(storageService.getFileByLocationId(metadata.getQppLocator()));
	}

	/**
	 * Process to ensure the file is an unprocessed cpc+ file and marks the file as processed
	 *
	 * @param fileId Identifier of the CPC+ file
	 * @return Success or failure message.
	 */
	@Override
	public String processFileById(String fileId, String orgName) {
		Metadata metadata = getMetadataById(fileId);
		if(Constants.CPC_ORG.equalsIgnoreCase(orgName)) {
			metadata.setCpcProcessed(true);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return FILE_FOUND_PROCESSED;
		} else if (Constants.RTI_ORG.equalsIgnoreCase(orgName)) {
			metadata.setRtiProcessed(true);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return FILE_FOUND_PROCESSED;
		} else {
			return FILE_NOT_FOUND;
		}
	}

	/**
	 * Process to ensure the file is a processed cpc+ file and marks the file as unprocessed
	 *
	 * @param fileId Identifier of the CPC+ file
	 * @param orgName Idenifier of which organization to process files for.
	 * @return Success or failure message.
	 */
	@Override
	public String unprocessFileById(String fileId, String orgName) {
		Metadata metadata = getMetadataById(fileId);
		if (Constants.CPC_ORG.equalsIgnoreCase(orgName)) {
			metadata.setCpcProcessed(false);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return FILE_FOUND_UNPROCESSED;
		} else if (Constants.RTI_ORG.equalsIgnoreCase(orgName)) {
			metadata.setRtiProcessed(false);
			CompletableFuture<Metadata> metadataFuture = dbService.write(metadata);
			metadataFuture.join();
			return FILE_FOUND_UNPROCESSED;
		} else {
			return FILE_NOT_FOUND;
		}
	}

	@Override
	public Metadata getMetadataById(String fileId) {
		Metadata metadata = dbService.getMetadataById(fileId);
		if (metadata == null) {
			throw new NoFileInDatabaseException(FILE_NOT_FOUND);
		} else if (!isCpcFile(metadata)) {
			throw new InvalidFileTypeException(INVALID_FILE);
		}
		return metadata;
	}

	/**
	 * Service to transform a {@link Metadata} list into the {@link UnprocessedCpcFileData}
	 *
	 * @param metadataList object to hold the list of {@link Metadata} from DynamoDb
	 * @return transformed list of {@link UnprocessedCpcFileData}
	 */
	private List<UnprocessedCpcFileData> transformMetaDataToUnprocessedCpcFileData(List<Metadata> metadataList) {
		return metadataList.stream().map(UnprocessedCpcFileData::new).collect(Collectors.toList());
	}

	/**
	 * Determines if the file is unprocessed and is CPC+
	 *
	 * @param metadata Data to be determined valid or invalid
	 * @return result of the check
	 */
	private boolean isAnUnprocessedCpcFile(Metadata metadata) {
		return isCpcFile(metadata) && (!metadata.getCpcProcessed() || !metadata.getRtiProcessed());
	}

	/**
	 * Determines if the file is a CPC+ submission
	 *
	 * @param metadata Data to be determined valid or invalid
	 * @return result of the check
	 */
	private boolean isCpcFile(Metadata metadata) {
		return metadata != null && metadata.getCpc() != null;
	}
}
