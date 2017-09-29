package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DbServiceImpl extends InOrderAsyncActionService<Metadata, Metadata>
		implements DbService {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private DynamoDBMapper mapper;

	public CompletableFuture<Metadata> write(Metadata meta) {
		API_LOG.info("Writing item to DynamoDB");
		return actOnItem(meta);
	}

	@Override
	protected Metadata asynchronousAction(Metadata meta) {
		mapper.save(meta);
		API_LOG.info("Wrote item to DynamoDB");
		return meta;
	}
}
