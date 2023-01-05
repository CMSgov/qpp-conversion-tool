package gov.cms.qpp.conversion.api.services.internal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.DbService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	private static final Logger API_LOG = LoggerFactory.getLogger(DbServiceImpl.class);
	private static final int LIMIT = 4;

	private final Optional<DynamoDBMapper> mapper;
	private final Environment environment;

	public DbServiceImpl(TaskExecutor taskExecutor, Optional<DynamoDBMapper> mapper, Environment environment) {
		super(taskExecutor);
		this.mapper = mapper;
		this.environment = environment;
	}

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

		if (!StringUtils.isEmpty(noAudit)) {
			API_LOG.warn("Not writing metadata information");
			return CompletableFuture.completedFuture(Metadata.create());
		}

		API_LOG.info("Writing item to DynamoDB");

		return actOnItem(meta);
	}

	/**
	 * Paginated Query of DynamoDb to limit items received as to remove issues with slowness/timeouts
	 *
	 * @param orgAttribute controls organizational choice when retrieving data
	 * @return
	 */
	public List<Metadata> getUnprocessedPcfMetaData(String orgAttribute) {
		if (mapper.isPresent()) {
			API_LOG.info("Getting list of unprocessed Pcf metadata...");

			String cpcConversionStartDate = Optional.ofNullable(
				environment.getProperty(Constants.CPC_PLUS_UNPROCESSED_FILE_SEARCH_DATE_VARIABLE)).orElse("");
			String year = cpcConversionStartDate.substring(0, 4);
			String indexName = Constants.DYNAMO_PCF_ATTRIBUTE + "-" + orgAttribute + "-index";

			return IntStream.range(0, Constants.CPC_DYNAMO_PARTITIONS).mapToObj(partition -> {
				Map<String, AttributeValue> valueMap = new HashMap<>();
				valueMap.put(":pcfValue", new AttributeValue().withS(Constants.PCF_DYNAMO_PARTITION_START + partition));
				valueMap.put(":pcfProcessedValue", new AttributeValue().withS("false#"+year));

				DynamoDBQueryExpression<Metadata> metadataQuery = new DynamoDBQueryExpression<Metadata>()
					.withIndexName(indexName)
					.withKeyConditionExpression(Constants.DYNAMO_PCF_ATTRIBUTE + " = :pcfValue and begins_with("
						+ orgAttribute + ", :pcfProcessedValue)")
					.withExpressionAttributeValues(valueMap)
					.withConsistentRead(false);

				return mapper.get().query(Metadata.class, metadataQuery).stream().limit(10);
			}).flatMap(Function.identity()).collect(Collectors.toList());
		} else {
			API_LOG.warn("Could not get unprocessed PCF metadata because the dynamodb mapper is absent");
			return Collections.emptyList();
		}
	}

	/**
	 * Queries the database table for a {@link Metadata} with a specific uuid
	 *
	 * @param uuid Identifier to query on
	 * @return Metadata found
	 */
	public Metadata getMetadataById(String uuid) {
		if (mapper.isPresent()) {
			API_LOG.info("Read item {} from DynamoDB", uuid);
			return mapper.get().load(Metadata.class, uuid);
		} else {
			API_LOG.warn("Skipping reading of item from DynamoDB with UUID {} because the dynamodb mapper is absent", uuid);
			return null;
		}
	}

	/**
	 * Actually does the write to DynamoDB.
	 *
	 * @param meta The metadata to write.
	 * @return The written metadata
	 */
	@Override
	protected Metadata asynchronousAction(Metadata meta) {
		if (mapper.isPresent()) {
			mapper.get().save(meta);
			API_LOG.info("Wrote item to DynamoDB with UUID {}", meta.getUuid());
		} else {
			API_LOG.warn("Skipping writing of item to DynamoDB with UUID {} because the dynamodb mapper is absent", meta.getUuid());
		}
		return meta;
	}

	@Override
	protected String getActionName() {
		return "Write Metadata";
	}
}
