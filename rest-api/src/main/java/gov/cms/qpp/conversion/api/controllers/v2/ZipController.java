package gov.cms.qpp.conversion.api.controllers.v2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.exceptions.InvalidPurposeException;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.ConvertResponse;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;

@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping(path = "/zip", headers = {"Accept=" + Constants.V2_API_ACCEPT})
public class ZipController {

	private static final Logger API_LOG = LoggerFactory.getLogger(ZipController.class);
	private static final int MAX_PURPOSE_LENGTH = 25;

	protected final QrdaService qrdaService;
	protected final ValidationService validationService;
	protected final AuditService auditService;

	/**
	 * init dependencies
	 *
	 * @param qrdaService {@link QrdaService} to perform QRDA to QPP conversion
	 * @param validationService {@link ValidationService} to perform post conversion validation
	 * @param auditService {@link AuditService} to persist audit information
	 */
	public ZipController(QrdaService qrdaService, ValidationService validationService, AuditService auditService) {
		this.qrdaService = qrdaService;
		this.validationService = validationService;
		this.auditService = auditService;
	}

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @param file Uploaded zip file
	 * @param purpose the purpose for the conversion
	 * @return Valid json or error json content
	 */
	@PostMapping
	public ResponseEntity<List<ConvertResponse>> uploadQrdaFile(
		@RequestParam(name = "file") MultipartFile file,
		@RequestHeader(required = false, name = "Purpose") String purposeHeader) {
		if (!StringUtils.isEmpty(purposeHeader)) {
			if (purposeHeader.length() > MAX_PURPOSE_LENGTH) {
				throw new InvalidPurposeException("Given Purpose (header) is too large. Max length is "
						+ MAX_PURPOSE_LENGTH + ", yours was " + purposeHeader.length());
			}
			API_LOG.info("Conversion request received for " + purposeHeader);
		} else {
			purposeHeader = null; // if it's an empty string, make it null
			API_LOG.info("Conversion request received");
		}

		String originalFilename = file.getOriginalFilename();
		String purpose = purposeHeader;
		
		File tempFile;
		List<ConvertResponse> responses = new ArrayList<>();
		try {
			tempFile = File.createTempFile("zipUpload", null);
			file.transferTo(tempFile);
			ZipFile zipFile = new ZipFile(tempFile);
			responses = zipFile.stream()
			.map(entry -> convert(originalFilename, zipFile, entry, purpose))
			.map(report -> respond(report))
			.collect(Collectors.toList());
			tempFile.delete();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<List<ConvertResponse>>(responses, httpHeaders, HttpStatus.CREATED);
	}

	private ConversionReport convert(String name, ZipFile zipFile, ZipEntry entry, String purpose) {
		try {
			InputStream inputStream = zipFile.getInputStream(entry);
			ConversionReport conversionReport = qrdaService.convertQrda3ToQpp(new InputStreamSupplierSource(name, inputStream, purpose));
			validationService.validateQpp(conversionReport);
			return conversionReport;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private ConvertResponse respond(ConversionReport report) {
		Metadata metadata = audit(report);
		ConvertResponse response = new ConvertResponse();
		if (metadata != null) {
			response.setLocation(metadata.getUuid());
		} else {
			API_LOG.info("Metadata is null");
		}
		response.setQpp(report.getEncoded().toObject());
		response.setWarnings(report.getWarnings());
		return response;
	}

	private Metadata audit(ConversionReport conversionReport) {
		try {
			CompletableFuture<Metadata> metadata = auditService.success(conversionReport);
			return metadata == null ? null : metadata.get();
		} catch (InterruptedException | ExecutionException exception) { //NOSONAR
			throw new AuditException(exception);
		}
	}
}
