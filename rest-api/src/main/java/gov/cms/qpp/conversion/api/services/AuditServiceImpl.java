package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.helper.MetadataHelper;
import gov.cms.qpp.conversion.api.helper.MetadataHelper.Outcome;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AuditServiceImpl implements AuditService {

	@Autowired
	private StorageService storageService;

	@Autowired
	private DbService dbService;

	/**
	 * Audit a successful conversion.
	 *
	 * @param conversionReport report of the conversion
	 * @return future
	 */
	@Override
	public CompletableFuture<Void> success(Converter.ConversionReport conversionReport) {
		Metadata metadata = initMetadata(conversionReport.getDecoded(), Outcome.SUCCESS);
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
		Metadata metadata = initMetadata(conversionReport.getDecoded(), Outcome.CONVERSION_ERROR);
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
		Metadata metadata = initMetadata(conversionReport.getDecoded(), Outcome.VALIDATION_ERROR);
		CompletableFuture<Void> allWrites = CompletableFuture.allOf(
				storeContent(conversionReport.streamDetails()).thenAccept(metadata::setValidationErrorLocator),
				storeContent(conversionReport.getEncoded().contentStream()).thenAccept(metadata::setQppLocator),
				storeContent(conversionReport.getFileInput()).thenAccept(metadata::setSubmissionLocator));
		return allWrites.whenComplete((nada, thrown) -> persist(metadata, thrown));
	}

	private Metadata initMetadata(Node decoded, MetadataHelper.Outcome outcome) {
		return MetadataHelper.generateMetadata(decoded, outcome);
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
