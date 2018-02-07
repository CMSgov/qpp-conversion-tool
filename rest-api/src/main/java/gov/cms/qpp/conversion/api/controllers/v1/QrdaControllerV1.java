package gov.cms.qpp.conversion.api.controllers.v1;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@RequestMapping("/")
@CrossOrigin
public class QrdaControllerV1 {
	private static final Logger API_LOG = LoggerFactory.getLogger(QrdaControllerV1.class);

	private QrdaService qrdaService;
	private ValidationService validationService;
	private AuditService auditService;

	/**
	 * init dependencies
	 *
	 * @param qrdaService {@link QrdaService} to perform QRDA -> QPP conversion
	 * @param validationService {@link ValidationService} to perform post conversion validation
	 * @param auditService {@link AuditService} to persist audit information
	 */
	public QrdaControllerV1(final QrdaService qrdaService, final ValidationService validationService,
							final AuditService auditService) {
		this.qrdaService = qrdaService;
		this.validationService = validationService;
		this.auditService = auditService;
	}

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @param file Uploaded file
	 * @return Valid json or error json content
	 */
	@PostMapping(headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<String> uploadQrdaFile(@RequestParam MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		API_LOG.info("Conversion request received");

		Converter.ConversionReport conversionReport = qrdaService.convertQrda3ToQpp(
				new InputStreamSupplierSource(originalFilename, inputStream(file)));

		validationService.validateQpp(conversionReport);

		auditService.success(conversionReport);

		API_LOG.info("Conversion request succeeded");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(conversionReport.getEncoded().toString(), httpHeaders, HttpStatus.CREATED);
	}

	/**
	 * Input stream from a file
	 *
	 * @param file the attachment
	 * @return an input stream from the {@link MultipartFile}
	 */
	InputStream inputStream(MultipartFile file) {
		try {
			return file.getInputStream();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
