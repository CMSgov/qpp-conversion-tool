package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import java.io.InputStream;
import java.util.List;

/**
 * Service interface to handle processing cpc+ files
 */
public interface CpcFileService {
	List<UnprocessedCpcFileData> getUnprocessedCpcPlusFiles();

	InputStream getFileById(String fileId);
}
