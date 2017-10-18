package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.helper.MetadataHelper;
import gov.cms.qpp.conversion.api.helper.MetadataHelper.Outcome;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AuditServiceImpl implements AuditService {
	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	@Autowired
	private StorageService storageService;

	@Autowired
	private DbService dbService;

	@Autowired
	private Environment environment;

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
		Metadata metadata = initMetadata(conversionReport, Outcome.SUCCESS);
		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(conversionReport.getFileInput()).thenAccept(metadata::setSubmissionLocator),
				storeContent(conversionReport.getEncoded().contentStream()).thenAccept(metadata::setQppLocator));
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
		Metadata metadata = initMetadata(conversionReport, Outcome.CONVERSION_ERROR);
		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(conversionReport.streamDetails()).thenAccept(metadata::setConversionErrorLocator),
				storeContent(conversionReport.getFileInput()).thenAccept(metadata::setSubmissionLocator));
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

		Metadata metadata = initMetadata(conversionReport, Outcome.VALIDATION_ERROR);
		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(conversionReport.streamRawValidationDetails()).thenAccept(metadata::setRawValidationErrorLocator),
				storeContent(conversionReport.streamDetails()).thenAccept(metadata::setValidationErrorLocator),
				storeContent(conversionReport.getEncoded().contentStream()).thenAccept(metadata::setQppLocator),
				storeContent(conversionReport.getFileInput()).thenAccept(metadata::setSubmissionLocator));
		return allWrites.whenComplete((nada, thrown) -> persist(metadata, thrown));
	}

	private boolean noAudit() {
		String noAudit = environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE);
		boolean returnValue = noAudit != null && !noAudit.isEmpty();

		if (returnValue) {
			API_LOG.info("Not writing audit information.");
		}

		return returnValue;
	}

	private Metadata initMetadata(Converter.ConversionReport report, MetadataHelper.Outcome outcome) {
		Metadata metadata = MetadataHelper.generateMetadata(report.getDecoded(), outcome);
		metadata.setFileName(report.getFilename());
		return metadata;
	}

	private CompletableFuture<String> storeContent(InputStream content) {
		UUID key = UUID.randomUUID();
		return storageService.store(key.toString(), content);
	}

	private CompletableFuture<Metadata> persist(Metadata metadata, Throwable thrown) {
		if (thrown != null) {
			throw new AuditException(thrown);
		}
		return dbService.write(metadata);
	}
}
