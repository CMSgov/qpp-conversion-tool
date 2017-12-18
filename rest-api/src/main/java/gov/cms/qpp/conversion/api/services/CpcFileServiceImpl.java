package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

/**
 * Service for handling Cpc File meta data
 */
@Service
public class CpcFileServiceImpl implements CpcFileService {

	public static final String FILE_NOT_FOUND = "File not found!";

	@Autowired
	private DbService dbService;

	@Autowired
	private StorageService storageService;

	/**
	 * Calls the DbService for unprocessed metadata to transform into UnprocessedCpcFileData
	 *
	 * @return List of {@link UnprocessedCpcFileData}
	 */
	@Override
	public List<UnprocessedCpcFileData> getUnprocessedCpcPlusFiles() {
		List<Metadata> metadata = dbService.getUnprocessedCpcPlusMetaData();

		return transformMetaDataToUnprocessedCpcFileData(metadata);
	}

	/**
	 * Retrieves the file location id and retrieves the file if it is an unprocessed cpc+ file
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return file contents as a {@link String}
	 * @throws IOException
	 */
	public InputStreamResource getFileById(String fileId) throws IOException {
		Metadata metadata = dbService.getMetadataById(fileId);
		if (metadata != null && metadata.getCpc() && !metadata.getCpcProcessed()) {
			return new InputStreamResource(storageService.getFileByLocationId(metadata.getSubmissionLocator()));
		} else {
			throw new NoFileInDatabaseException(FILE_NOT_FOUND);
		}
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
}
