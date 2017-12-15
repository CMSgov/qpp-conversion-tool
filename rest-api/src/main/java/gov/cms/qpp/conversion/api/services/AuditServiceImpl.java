package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.Source;
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

		API_LOG.info("Writing success audit information");

		Metadata metadata = initMetadata(conversionReport, Outcome.SUCCESS);

		Source qrdaSource = conversionReport.getQrdaSource();
		Source qppSource = conversionReport.getQppSource();

		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(qrdaSource.toInputStream(), qrdaSource.getSize()).thenAccept(metadata::setSubmissionLocator),
				storeContent(qppSource.toInputStream(), qppSource.getSize()).thenAccept(metadata::setQppLocator));
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
				storeContent(validationErrorSource.toInputStream(), validationErrorSource.getSize()).thenAccept(metadata::setConversionErrorLocator),
				storeContent(qrdaSource.toInputStream(), qrdaSource.getSize()).thenAccept(metadata::setSubmissionLocator));
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
				storeContent(rawValidationErrorSource.toInputStream(), rawValidationErrorSource.getSize()).thenAccept(metadata::setRawValidationErrorLocator),
				storeContent(validationErrorSource.toInputStream(), validationErrorSource.getSize()).thenAccept(metadata::setValidationErrorLocator),
				storeContent(qppSource.toInputStream(), qppSource.getSize()).thenAccept(metadata::setQppLocator),
				storeContent(qrdaSource.toInputStream(), qrdaSource.getSize()).thenAccept(metadata::setSubmissionLocator));
		return allWrites.whenComplete((nada, thrown) -> persist(metadata, thrown));
	}

	private boolean noAudit() {
		String noAudit = environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE);
		boolean returnValue = noAudit != null && !noAudit.isEmpty();

		if (returnValue) {
			API_LOG.warn("Not writing audit information.");
		}

		return returnValue;
	}

	private Metadata initMetadata(Converter.ConversionReport report, MetadataHelper.Outcome outcome) {
		Metadata metadata = MetadataHelper.generateMetadata(report.getDecoded(), outcome);
		metadata.setFileName(report.getQrdaSource().getName());
		return metadata;
	}

	private CompletableFuture<String> storeContent(InputStream content, long size) {
		UUID key = UUID.randomUUID();
		return storageService.store(key.toString(), content, size);
	}

	private CompletableFuture<Metadata> persist(Metadata metadata, Throwable thrown) {
		if (thrown != null) {
			throw new AuditException(thrown);
		}
		return dbService.write(metadata);
	}
}
