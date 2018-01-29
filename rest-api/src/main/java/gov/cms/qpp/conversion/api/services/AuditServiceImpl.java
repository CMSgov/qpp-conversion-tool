package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.helper.MetadataHelper;
import gov.cms.qpp.conversion.api.helper.MetadataHelper.Outcome;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.util.MeasuredInputStreamSupplier;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Service for storing {@link Metadata} by {@link Converter.ConversionReport} outcome
 */
@Service
public class AuditServiceImpl implements AuditService {
	private static final Logger API_LOG = LoggerFactory.getLogger(AuditServiceImpl.class);

	private StorageService storageService;
	private DbService dbService;
	private Environment environment;

	/**
	 * initialize
	 *
	 * @param storageService save conversion output
	 * @param dbService save conversion metadata
	 * @param environment hooks to the environment in which the application runs
	 */
	public AuditServiceImpl(final StorageService storageService, final DbService dbService,
							final Environment environment) {
		this.storageService = storageService;
		this.dbService = dbService;
		this.environment = environment;
	}

	/**
	 * Audit a successful conversion.
	 *
	 * @param conversionReport report of the conversion
	 * @return future
	 */
	@Override
	public CompletableFuture<Void> success(Converter.ConversionReport conversionReport) {
		if (noAudit()) {
			return null;
		}

		API_LOG.info("Writing success audit information");

		Metadata metadata = initMetadata(conversionReport, Outcome.SUCCESS);

		Source qrdaSource = conversionReport.getQrdaSource();
		Source qppSource = conversionReport.getQppSource();

		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(qrdaSource).thenAccept(metadata::setSubmissionLocator),
				storeContent(qppSource).thenAccept(metadata::setQppLocator));
		return allWrites.whenComplete((nada, thrown) -> persist(metadata, thrown));
	}

	/**
	 * Audit a failed QPP conversion.
	 *
	 * @param conversionReport report of the conversion
	 * @return future
	 */
	@Override
	public CompletableFuture<Void> failConversion(Converter.ConversionReport conversionReport) {
		if (noAudit()) {
			return null;
		}

		API_LOG.info("Writing audit information for a conversion failure scenario");

		Metadata metadata = initMetadata(conversionReport, Outcome.CONVERSION_ERROR);

		Source qrdaSource = conversionReport.getQrdaSource();
		Source validationErrorSource = conversionReport.getValidationErrorsSource();

		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(validationErrorSource).thenAccept(metadata::setConversionErrorLocator),
				storeContent(qrdaSource).thenAccept(metadata::setSubmissionLocator));
		return allWrites.whenComplete((nada, thrown) -> persist(metadata, thrown));
	}

	/**
	 * Audit a failed submission validation.
	 *
	 * @param conversionReport report of the conversion
	 * @return future
	 */
	@Override
	public CompletableFuture<Void> failValidation(Converter.ConversionReport conversionReport) {
		if (noAudit()) {
			return null;
		}

		API_LOG.info("Writing audit information for a validation failure scenario");

		Source qrdaSource = conversionReport.getQrdaSource();
		Source qppSource = conversionReport.getQppSource();
		Source validationErrorSource = conversionReport.getValidationErrorsSource();
		Source rawValidationErrorSource = conversionReport.getRawValidationErrorsOrEmptySource();

		Metadata metadata = initMetadata(conversionReport, Outcome.VALIDATION_ERROR);
		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(rawValidationErrorSource).thenAccept(metadata::setRawValidationErrorLocator),
				storeContent(validationErrorSource).thenAccept(metadata::setValidationErrorLocator),
				storeContent(qppSource).thenAccept(metadata::setQppLocator),
				storeContent(qrdaSource).thenAccept(metadata::setSubmissionLocator));
		return allWrites.whenComplete((nada, thrown) -> persist(metadata, thrown));
	}

	/**
	 * Determines if the No Audit Environment variable was passed
	 *
	 * @return the status of auditing
	 */
	private boolean noAudit() {
		String noAudit = environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE);
		boolean returnValue = noAudit != null && !noAudit.isEmpty();

		if (returnValue) {
			API_LOG.warn("Not writing audit information.");
		}

		return returnValue;
	}

	/**
	 * Initializes {@link Metadata} from the {@link Converter.ConversionReport} and conversion outcome
	 *
	 * @param report Object containing metadata information
	 * @param outcome Status of the conversion
	 * @return Constructed metadata
	 */
	private Metadata initMetadata(Converter.ConversionReport report, MetadataHelper.Outcome outcome) {
		Metadata metadata = MetadataHelper.generateMetadata(report.getDecoded(), outcome);
		metadata.setFileName(report.getQrdaSource().getName());
		return metadata;
	}

	/**
	 * Calls the {@link StorageService} to store an {@link InputStream}.
	 *
	 * @param sourceToStore The {@link Source} to store.
	 * @return A {@link CompletableFuture} that represents storing the information.
	 */
	private CompletableFuture<String> storeContent(Source sourceToStore) {
		UUID key = UUID.randomUUID();
		Supplier<InputStream> stream = MeasuredInputStreamSupplier.terminallyTransformInputStream(sourceToStore.toInputStream());
		return storageService.store(key.toString(), stream, sourceToStore.getSize());
	}

	/**
	 * Calls the {@link DbService} to store the {@link Metadata} in a database.
	 *
	 * @param metadata The {@link Metadata} to save.
	 * @param thrown A {@link Throwable} from a previous step.
	 * @return A {@link CompletableFuture} that represents saving {@link Metadata} to a database.
	 */
	private CompletableFuture<Metadata> persist(Metadata metadata, Throwable thrown) {
		if (thrown != null) {
			throw new AuditException(thrown);
		}
		return dbService.write(metadata);
	}
}
