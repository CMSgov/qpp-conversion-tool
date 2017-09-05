package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.entities.QppFileEntity;
import gov.cms.qpp.conversion.api.repositories.QppFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamoDbServiceImpl extends AsyncActionService<QppFileEntity> implements DynamoDbService {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private QppFileRepository qppFileRepository;

	@Override
	public void addItem(final QppFileEntity qppFileEntity) {
		actOnItem(qppFileEntity);
	}

	protected boolean asynchronousAction(QppFileEntity objectToActOn) {
		API_LOG.info("Writing item to DynamoDb");
		qppFileRepository.save(objectToActOn);
		return true;
	}
}
