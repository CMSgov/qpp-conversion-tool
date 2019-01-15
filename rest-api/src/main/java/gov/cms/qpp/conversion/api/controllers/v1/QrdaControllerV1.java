package gov.cms.qpp.conversion.api.controllers.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.api.controllers.SkeletalQrdaController;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@RequestMapping(path = "/", headers = {"Accept=" + Constants.V1_API_ACCEPT})
public class QrdaControllerV1 extends SkeletalQrdaController<String> {

	public QrdaControllerV1(QrdaService qrdaService, ValidationService validationService, AuditService auditService) {
		super(qrdaService, validationService, auditService);
	}

	@Override
	protected String respond(ConversionReport report) {
		return report.getEncodedWithMetadata().copyWithoutMetadata().toString(); // TODO toObject?
	}

}
