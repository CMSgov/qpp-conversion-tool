package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling Cpc File meta data
 */
@Service
public class CpcFileServiceImpl implements CpcFileService {

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

	public InputStream getFileById(String fileId) {
		String fileLocationId = dbService.getFileSubmissionLocationId(fileId);

		return storageService.getFileByLocationId(fileLocationId);
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
