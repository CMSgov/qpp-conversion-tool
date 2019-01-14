package gov.cms.qpp.conversion.api.internal.pii;

import gov.cms.qpp.conversion.api.model.CpcValidationInfo;
import gov.cms.qpp.conversion.api.model.CpcValidationInfoMap;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.pii.PiiValidator;

public class SpecPiiValidator implements PiiValidator {

	private final CpcValidationInfoMap file;

	public SpecPiiValidator(CpcValidationInfoMap file) {
		this.file = file;
	}

	@Override
	public void validateApmNpiCombination(Node node, NodeValidator validator) {
		String apm = node.getValue(ClinicalDocumentDecoder.PRACTICE_ID);
		String npi = node.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER);

		CpcValidationInfo spec = file.getApmToSpec().get(apm);
		if (spec == null) {
			validator.addWarning(Detail.forErrorAndNode(ErrorCode.INCORRECT_API_NPI_COMBINATION, node));
		} else {
			if (spec.getNpi() != null && !spec.getNpi().equalsIgnoreCase(npi)) {
				validator.addWarning(Detail.forErrorAndNode(ErrorCode.INCORRECT_API_NPI_COMBINATION, node));
			}
		}
	}

}
