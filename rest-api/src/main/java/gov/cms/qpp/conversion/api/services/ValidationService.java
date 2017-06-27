package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.encode.JsonWrapper;

public interface ValidationService {
	public void validateQpp(JsonWrapper qpp);
}
