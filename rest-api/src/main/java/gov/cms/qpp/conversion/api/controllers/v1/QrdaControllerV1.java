package gov.cms.qpp.conversion.api.controllers.v1;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.api.controllers.SkeletalQrdaController;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@RequestMapping(path = "/", headers = {"Accept=" + Constants.V1_API_ACCEPT})
public class QrdaControllerV1 extends SkeletalQrdaController<String> {

	/**
	 * Constructor to super class to initialize fields
	 * @param qrdaService {@link QrdaService} to perform QRDA to QPP conversion
	 * @param validationService {@link ValidationService} to perform post conversion validation
	 * @param auditService {@link AuditService} to persist audit information
	 */
	public QrdaControllerV1(QrdaService qrdaService, ValidationService validationService, AuditService auditService) {
		super(qrdaService, validationService, auditService);
	}

	@Override
	protected String respond(MultipartFile file, String checkedPurpose, HttpHeaders httpHeaders) {
		ConversionReport conversionReport = buildReport(file.getOriginalFilename(), inputStream(file), checkedPurpose);
		Metadata metadata = audit(conversionReport);
		if (null != metadata) {
			httpHeaders.add("Location", metadata.getUuid());
		}
		return report.getEncodedWithMetadata().copyWithoutMetadata().toString(); // TODO toObject?
	}

}
