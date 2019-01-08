package gov.cms.qpp.conversion.api.controllers.v2;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.api.controllers.SkeletalQrdaController;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.ConvertResponse;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.conversion.api.services.ValidationService;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@RequestMapping(path = "/", headers = {"Accept=" + Constants.V2_API_ACCEPT})
public class QrdaControllerV2 extends SkeletalQrdaController<ConvertResponse> {

	public QrdaControllerV2(QrdaService qrdaService, ValidationService validationService, AuditService auditService) {
		super(qrdaService, validationService, auditService);
	}

	@Override
	protected ConvertResponse respond(ConversionReport report) {
		ConvertResponse response = new ConvertResponse();
		response.setQpp(report.getEncoded().toObject());
		return response;
	}

}
