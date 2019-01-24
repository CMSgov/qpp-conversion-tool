package gov.cms.qpp.conversion.api.controllers.v2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.api.controllers.SkeletalQrdaController;
import gov.cms.qpp.conversion.api.model.ConvertResponse;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;

@RestController
@RequestMapping(path = "/", headers = {"Accept=application/zip"})
public class ZipController extends SkeletalQrdaController<List<ConvertResponse>>{

	private static final Logger API_LOG = LoggerFactory.getLogger(ZipController.class);

	/**
	 * Constructor to super class to initialize fields
	 * @param qrdaService {@link QrdaService} to perform QRDA to QPP conversion
	 * @param validationService {@link ValidationService} to perform post conversion validation
	 * @param auditService {@link AuditService} to persist audit information
	 */
	public ZipController(QrdaService qrdaService, ValidationService validationService, AuditService auditService) {
		super(qrdaService, validationService, auditService);
	}

	@Override
	protected List<ConvertResponse> respond(MultipartFile file, String checkedPurpose, HttpHeaders httpHeaders) {
		File tempFile;
		try {
			tempFile = File.createTempFile("zipUpload", null);
			file.transferTo(tempFile);
			ZipFile zipFile = new ZipFile(tempFile);
			List<ConvertResponse> responses = zipFile.stream()
			.map(entry -> buildResponseForEntry(zipFile, entry, checkedPurpose))
			.collect(Collectors.toList());
			if (!tempFile.delete()) {
				API_LOG.warn("Uploaded zip temp file not deleted.");
			}
			zipFile.close();
			return responses;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private ConvertResponse buildResponseForEntry(ZipFile zipFile, ZipEntry entry, String purpose) {
		try {
			InputStream inputStream = zipFile.getInputStream(entry);
			ConversionReport conversionReport = buildReport(entry.getName(), inputStream, purpose);
			ConvertResponse response = new ConvertResponse();
			response.setQpp(conversionReport.getEncoded().toObject());
			response.setWarnings(conversionReport.getWarnings());
			Metadata metadata = audit(conversionReport);
			if (null != metadata) {
				response.setLocation(metadata.getUuid());
			}
			return response;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

}
