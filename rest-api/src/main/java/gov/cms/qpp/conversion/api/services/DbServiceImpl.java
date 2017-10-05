package gov.cms.qpp.conversion.api.services;


import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import gov.cms.qpp.conversion.api.model.Metadata;

/**
 * Writes a {@link Metadata} object to DynamoDB.
 */
@Service
public class DbServiceImpl extends AnyOrderActionService<Metadata, Metadata>
		implements DbService {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	private static final String NO_AUDIT_ENV_VARIABLE = "NO_AUDIT";

	@Autowired
	private DynamoDBMapper mapper;

	@Autowired
	private Environment environment;

	/**
	 * Writes the passed in {@link Metadata} to DynamoDB.
	 *
	 * If the KMS_KEY environment variable is unspecified, nothing is written.  The {@link CompletableFuture} will hold an empty
	 * {@link Metadata} in this case.
	 *
	 * @param meta The metadata to write.
	 * @return A {@link CompletableFuture} that will hold the written Metadata.
	 */
	public CompletableFuture<Metadata> write(Metadata meta) {

		String noAudit = environment.getProperty(NO_AUDIT_ENV_VARIABLE);

		if (noAudit != null && !noAudit.isEmpty()) {
			API_LOG.info("Not writing audit information.");
			return CompletableFuture.completedFuture(new Metadata());
		}

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
