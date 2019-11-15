package gov.cms.qpp.conversion.api.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.exceptions.InvalidPurposeException;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@CrossOrigin(allowCredentials = "true")
public abstract class SkeletalQrdaController<T> {

	private static final Logger API_LOG = LoggerFactory.getLogger(SkeletalQrdaController.class);
	private static final int MAX_PURPOSE_LENGTH = 25;

	protected final QrdaService qrdaService;
	protected final ValidationService validationService;
	protected final AuditService auditService;

	protected abstract T respond(MultipartFile file, String checkedPurpose, HttpHeaders httpHeaders);

	/**
	 * init dependencies
	 *
	 * @param qrdaService {@link QrdaService} to perform QRDA to QPP conversion
	 * @param validationService {@link ValidationService} to perform post conversion validation
	 * @param auditService {@link AuditService} to persist audit information
	 */
	public SkeletalQrdaController(QrdaService qrdaService, ValidationService validationService, AuditService auditService) {
		this.qrdaService = qrdaService;
		this.validationService = validationService;
		this.auditService = auditService;
	}

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @param file Uploaded file
	 * @param purpose the purpose for the conversion
	 * @return Valid json or error json content
	 */
	@PostMapping
	@CrossOrigin(origins = "*")
	public ResponseEntity<T> uploadQrdaFile(
		@RequestParam(name = "file") MultipartFile file,
		@RequestHeader(required = false, name = "Purpose") String purpose) {

		if (!StringUtils.isEmpty(purpose)) {
			if (purpose.length() > MAX_PURPOSE_LENGTH) {
				throw new InvalidPurposeException("Given Purpose (header) is too large. Max length is "
						+ MAX_PURPOSE_LENGTH + ", yours was " + purpose.length());
			}
			API_LOG.info("Conversion request received for " + purpose);
		} else {
			purpose = null; // if it's an empty string, make it null
			API_LOG.info("Conversion request received");
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		T response = respond(file, purpose, httpHeaders);

		API_LOG.info("Conversion request succeeded");

		return new ResponseEntity<>(response, httpHeaders, HttpStatus.CREATED);
	}

	protected ConversionReport buildReport(String filename, InputStream inputStream, String purpose) {
		ConversionReport conversionReport = qrdaService.convertQrda3ToQpp(
				new InputStreamSupplierSource(filename, inputStream, purpose));
		validationService.validateQpp(conversionReport);
		return conversionReport;
	}

	/**
	 * Input stream from a file
	 *
	 * @param file the attachment
	 * @return an input stream from the {@link MultipartFile}
	 */
	public InputStream inputStream(MultipartFile file) {
		try {
			return file.getInputStream();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	protected Metadata audit(ConversionReport conversionReport) {
		try {
			CompletableFuture<Metadata> metadata = auditService.success(conversionReport);
			return metadata == null ? null : metadata.get();
		} catch (InterruptedException | ExecutionException exception) { //NOSONAR
			throw new AuditException(exception);
		}
	}

}