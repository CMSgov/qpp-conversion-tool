package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import java.util.List;

public interface CpcFileService {
	public List<UnprocessedCpcFileData> getUnprocessedCpcPlusFiles();
}
