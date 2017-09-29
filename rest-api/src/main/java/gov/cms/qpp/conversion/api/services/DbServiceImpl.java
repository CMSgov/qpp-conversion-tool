package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Writes a {@link Metadata} object to DynamoDB.
 */
@Service
public class DbServiceImpl extends AnyOrderAsyncActionService<Metadata, Metadata>
		implements DbService {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private DynamoDBMapper mapper;

	/**
	 * Writes the passed in {@link Metadata} to DynamoDB.
	 *
	 * @param meta The metadata to write.
	 * @return A {@link CompletableFuture} that will hold the written Metadata.
	 */
	public CompletableFuture<Metadata> write(Metadata meta) {
		API_LOG.info("Writing item to DynamoDB");
		return actOnItem(meta);
	}

	/**
	 * Actually does the write to DynamoDB.
	 *
	 * @param meta The metadata to write.
	 * @return The written metadata
	 */
	@Override
	protected Metadata asynchronousAction(Metadata meta) {
		mapper.save(meta);
		API_LOG.info("Wrote item to DynamoDB with UUID {}", meta.getUuid());
		return meta;
	}
}
