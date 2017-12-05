package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling Cpc File meta data
 */
@Service
public class CpcFilesServiceImpl implements CpcFileService {

	@Autowired
	private DbService dbService;

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
	 * Service to transform a {@link Metadata} list into the {@link UnprocessedCpcFileData}
	 *
	 * @param metadataList
	 * @return
	 */
	private List<UnprocessedCpcFileData> transformMetaDataToUnprocessedCpcFileData(List<Metadata> metadataList) {
		return metadataList.stream().map(UnprocessedCpcFileData::new).collect(Collectors.toList());
	}
}
