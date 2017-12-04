package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CpcFilesServiceImpl implements CpcFileService {

	@Autowired
	private DbService dbService;

	@Override
	public List<UnprocessedCpcFileData> getUnprocessedCpcPlusFiles() {
		List<Metadata> metadata = dbService.getUnprocessedCpcPlusMetaData();

		return transformMetaDataToUnprocessedCpcFileData(metadata);
	}

	/**
	 * Service to transform a {@link Metadata} list into the {@Link UnprocessedCpcFileData}
	 *
	 * @param metadataList
	 * @return
	 */
	private List<UnprocessedCpcFileData> transformMetaDataToUnprocessedCpcFileData(List<Metadata> metadataList) {
		List<UnprocessedCpcFileData> unprocessedCpcFileDataList = new ArrayList<>();
		metadataList.forEach(data -> {
			UnprocessedCpcFileData unprocessedCpcFileData = new UnprocessedCpcFileData(data);
			unprocessedCpcFileDataList.add(unprocessedCpcFileData);
		});

		return unprocessedCpcFileDataList;
	}
}
