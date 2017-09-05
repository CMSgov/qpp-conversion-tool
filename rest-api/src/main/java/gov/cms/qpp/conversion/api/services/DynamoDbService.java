package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.entities.QppFileEntity;

public interface DynamoDbService {
	public void addItem(QppFileEntity qppFileEntity);
}
