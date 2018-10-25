package gov.cms.qpp.conversion.api.services;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;

/**
 * Writes a {@link Metadata} object to the database.
 */
@Service
public class DbServiceImpl extends AnyOrderActionService<Metadata, Metadata>
		implements DbService {

	private static final Logger API_LOG = LoggerFactory.getLogger(DbServiceImpl.class);
	public static final Long START_OF_UNALLOWED_CONVERSION_TIME = Instant.parse("2018-01-02T04:59:59.999Z").toEpochMilli();

	private final Environment environment;
	private final MetadataRepository repository;

	public DbServiceImpl(TaskExecutor taskExecutor, Environment environment, MetadataRepository repository) {
		super(taskExecutor);

		this.environment = environment;
		this.repository = repository;
	}

	/**
	 * Writes the passed in {@link Metadata} to the database.
	 *
	 * @param meta The metadata to write.
	 * @return A {@link CompletableFuture} that will hold the written Metadata.
	 */
	@Override
	public CompletableFuture<Metadata> write(Metadata meta) {
		Objects.requireNonNull(meta, "meta");

		String noAudit = environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE);

		if (!StringUtils.isEmpty(noAudit)) {
			API_LOG.warn("Not writing metadata item '{}' because auditing is disabled with the '{}' property",
					meta.getUuid(), Constants.NO_AUDIT_ENV_VARIABLE);
			return CompletableFuture.completedFuture(new Metadata());
		}

		API_LOG.info("Writing metadata item '{}' to the database", meta.getUuid());

		return actOnItem(meta);
	}

	/**
	 * Queries the database for unprocessed {@link Metadata}.
	 *
	 * @return {@link List} of unprocessed {@link Metadata}
	 */
	public List<Metadata> getUnprocessedCpcPlusMetaData() {
		API_LOG.info("Getting list of unprocessed CPC+ metadata");
		List<Metadata> unprocessedMetadata = repository.getUnprocessedCpcPlusMetadata(START_OF_UNALLOWED_CONVERSION_TIME);
		if (unprocessedMetadata == null || unprocessedMetadata.isEmpty()) {
			API_LOG.info("Could not find any unprocessed CPC+ metadata");
		} else {
			API_LOG.info("Found '{}' unprocessed CPC+ metadata items", unprocessedMetadata.size());
		}
		return unprocessedMetadata;
	}

	/**
	 * Queries the database table for a {@link Metadata} with a specific uuid
	 *
	 * @param uuid Identifier to query on
	 * @return Metadata found
	 */
	public Metadata getMetadataById(String uuid) {
		API_LOG.info("Reading item '{}' in database", uuid);
		Optional<Metadata> metadata = repository.findById(uuid);
		if (metadata.isPresent()) {
			API_LOG.info("Found item '{}' in database", uuid);
			return metadata.get();
		}
		API_LOG.warn("Could not find item '{}' in database", uuid);
		return null;
	}

	/**
	 * Write the {@link Metadata} object to the database.
	 *
	 * @param meta The {@link Metadata} to write.
	 * @return The written {@link Metadata}.
	 */
	@Override
	protected Metadata asynchronousAction(Metadata metadata) {
		API_LOG.info("Wrote item '{}' to database", metadata.getUuid());
		return repository.save(metadata);
	}
}
