package gov.cms.qpp.conversion.api.internal.pii;

import gov.cms.qpp.conversion.api.model.CpcValidationInfo;
import gov.cms.qpp.conversion.api.model.CpcValidationInfoMap;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.pii.PiiValidator;

import java.util.Arrays;
import java.util.List;

public class SpecPiiValidator implements PiiValidator {

	private final CpcValidationInfoMap file;

	public SpecPiiValidator(CpcValidationInfoMap file) {
		this.file = file;
	}

	@Override
	public void validateApmNpiCombination(Node node, NodeValidator validator) {
		String apm = node.getValue(ClinicalDocumentDecoder.PRACTICE_ID);
		List<String> tinList = Arrays.asList(
			node.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER).split(","));

		for (final String tin : tinList) {
			CpcValidationInfo spec = file.getApmToSpec().get(tin);
			if (spec == null) {
				validator.addWarning(Detail.forErrorAndNode(ErrorCode.INCORRECT_API_NPI_COMBINATION, node));
			} else {
				if (spec.getApm() != null && !spec.getApm().equalsIgnoreCase(apm)) {
					validator.addWarning(Detail.forErrorAndNode(ErrorCode.INCORRECT_API_NPI_COMBINATION, node));
				}
			}
		}
	}

}
