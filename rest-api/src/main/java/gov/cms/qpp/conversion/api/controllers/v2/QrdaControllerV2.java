package gov.cms.qpp.conversion.api.controllers.v2;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.api.controllers.SkeletalQrdaController;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.ConvertResponse;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@RequestMapping(path = "/", headers = {"Accept=" + Constants.V2_API_ACCEPT})
public class QrdaControllerV2 extends SkeletalQrdaController<ConvertResponse> {

	/**
	 * Constructor to super class to initialize fields
	 * @param qrdaService {@link QrdaService} to perform QRDA to QPP conversion
	 * @param validationService {@link ValidationService} to perform post conversion validation
	 * @param auditService {@link AuditService} to persist audit information
	 */
	public QrdaControllerV2(QrdaService qrdaService, ValidationService validationService, AuditService auditService) {
		super(qrdaService, validationService, auditService);
	}
	
	/**
	 * Implementation of abstract method. During a file upload this will create a response
	 * to match the new version 2 API.
	 * 
	 * The version 2 API responds with validation warnings as well as the encoded QPP JsonWrapper.
	 */
	@Override
	protected ConvertResponse respond(MultipartFile file, String checkedPurpose, HttpHeaders httpHeaders) {
		ConversionReport conversionReport = buildReport(file.getOriginalFilename(), inputStream(file), checkedPurpose);
		ConvertResponse response = new ConvertResponse();
		response.setQpp(conversionReport.getEncoded().toObject());
		response.setWarnings(conversionReport.getWarnings());
		Metadata metadata = audit(conversionReport);
		if (null != metadata) {
			httpHeaders.add("Location", metadata.getUuid());
			response.setLocation(metadata.getUuid());
		}
		return response;
	}

}
