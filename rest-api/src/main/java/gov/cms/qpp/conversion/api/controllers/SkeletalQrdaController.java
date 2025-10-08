package gov.cms.qpp.conversion.api.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
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
@Tag(name="Conversion")
@RestController
@CrossOrigin
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
	@Operation(summary = "Converts QRDA3 XML to QPP JSON", description = "Converts QRDA3 XML to QPP JSON. Provide a QRDA3 XML file as a multipart file. The response body will contain the QPP JSON upon success or error JSON on failure.")
	@Parameter(name = "Purpose", hidden = true)
	@PostMapping(consumes = {"multipart/form-data"})
	public ResponseEntity<T> uploadQrdaFile(
		@RequestParam(name = "file") MultipartFile file,
		@RequestHeader(required = false, name = "Purpose") String purpose) {

		if (!ObjectUtils.isEmpty(purpose)) {
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
		long startTime = System.currentTimeMillis();
		ConversionReport conversionReport = qrdaService.convertQrda3ToQpp(
				new InputStreamSupplierSource(filename, inputStream, purpose));
		if (API_LOG.isDebugEnabled()) {
			API_LOG.debug("qrdaService.convertQrda3ToQpp took {} ms", System.currentTimeMillis() - startTime);
		}
		
		startTime = System.currentTimeMillis();
		validationService.validateQpp(conversionReport);
		if (API_LOG.isDebugEnabled()) {
			API_LOG.debug("validationService.validateQpp took {} ms", System.currentTimeMillis() - startTime);
		}
		
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
			long startTime = System.currentTimeMillis();
			CompletableFuture<Metadata> metadata = auditService.success(conversionReport);
			Metadata result = metadata == null ? null : metadata.get();
			if (API_LOG.isDebugEnabled()) {
				API_LOG.debug("auditService.success().get() took {} ms", System.currentTimeMillis() - startTime);
			}
			return result;
		} catch (InterruptedException | ExecutionException exception) { //NOSONAR
			throw new AuditException(exception);
		}
	}

}