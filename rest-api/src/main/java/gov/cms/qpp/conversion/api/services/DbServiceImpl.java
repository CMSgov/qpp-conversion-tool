package gov.cms.qpp.conversion.api.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Writes a {@link Metadata} object to DynamoDB.
 */
@Service
public class DbServiceImpl extends AnyOrderActionService<Metadata, Metadata>
		implements DbService {

	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

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
	@Override
	public CompletableFuture<Metadata> write(Metadata meta) {

		String noAudit = environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE);

		if (noAudit != null && !noAudit.isEmpty()) {
			API_LOG.warn("Not writing metadata information");
			return CompletableFuture.completedFuture(new Metadata());
		}

		API_LOG.info("Writing item to DynamoDB");

		return actOnItem(meta);
	}

	/**
	 * Scans the DynamoDB table for unprocessed {@link Metadata}
	 *
	 * @return {@link List} of unprocessed {@link Metadata}
	 */
	public List<Metadata> getUnprocessedCpcPlusMetaData() {

		return IntStream.range(0, Constants.CPC_DYNAMO_PARTITIONS).mapToObj(partition -> {
			Map<String, AttributeValue> valueMap = new HashMap<>();
			valueMap.put(":cpcValue", new AttributeValue().withS(Constants.CPC_DYNAMO_PARTITION_START + partition));
			valueMap.put(":cpcProcessedValue", new AttributeValue().withS("false"));

			DynamoDBQueryExpression<Metadata> metadataQuery = new DynamoDBQueryExpression<Metadata>()
				.withIndexName("Cpc-CpcProcessed_CreateDate-index")
				.withKeyConditionExpression(Constants.DYNAMO_CPC_ATTRIBUTE + " = :cpcValue and begins_with(" +
					Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE + ", :cpcProcessedValue)")
				.withExpressionAttributeValues(valueMap)
				.withConsistentRead(false);

			return mapper.query(Metadata.class, metadataQuery).stream();
		}).flatMap(Function.identity()).collect(Collectors.toList());
	}

	/**
	 * Queries the database table for a {@link Metadata} with a specific uuid
	 *
	 * @param uuid Identifier to query on
	 * @return Metadata found
	 */
	public Metadata getMetadataById(String uuid) {
		return mapper.load(Metadata.class, uuid);
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
