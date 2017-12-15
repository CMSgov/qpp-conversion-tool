package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@RequestMapping("/")
@CrossOrigin
public class QrdaControllerV1 {
	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	@Autowired
	private QrdaService qrdaService;

	@Autowired
	private ValidationService validationService;

	@Autowired
	private AuditService auditService;

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @param file Uploaded file
	 * @return Valid json or error json content
	 * @throws IOException If errors occur during file upload or conversion
	 */
	@RequestMapping(method = RequestMethod.POST, headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<String> uploadQrdaFile(@RequestParam MultipartFile file) throws IOException {
		String originalFilename = file.getOriginalFilename();
		API_LOG.info("Conversion request received");

		Converter.ConversionReport conversionReport = qrdaService.convertQrda3ToQpp(
				new InputStreamSupplierSource(originalFilename, inputStreamSupplier(file), file.getSize()));

		validationService.validateQpp(conversionReport);

		auditService.success(conversionReport);

		API_LOG.info("Conversion request succeeded");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(conversionReport.getEncoded().toString(), httpHeaders, HttpStatus.CREATED);
	}

	/**
	 * Supplier for file input
	 *
	 * @param file the attachment
	 * @return a supplier that wraps the attachment's input stream retrieval
	 */
	Supplier<InputStream> inputStreamSupplier(MultipartFile file) {
		return () -> {
			try {
				return file.getInputStream();
			} catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		};
	}
}
