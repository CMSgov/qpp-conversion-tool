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
	public void validateApmTinNpiCombination(Node node, NodeValidator validator) {
		String apm = node.getValue(ClinicalDocumentDecoder.PRACTICE_ID);
		List<String> npiList = Arrays.asList(
			node.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER).split(","));
		List<String> tinList = Arrays.asList(
			node.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER).split(","));

		List<CpcValidationInfo> tinNpiCombinationList = file.getApmTinNpiCombination().get(apm);
		if (tinNpiCombinationList == null){
			validator.addWarning(Detail.forErrorAndNode(ErrorCode.INCORRECT_API_NPI_COMBINATION, node));
		} else {
			for (int index = 0; index < npiList.size(); index++) {
				String currentTin = tinList.get(index);
				String currentNpi = npiList.get(index);
				boolean isValidCombination = false;
				for (CpcValidationInfo tinNpiCombination : tinNpiCombinationList) {
					if (tinNpiCombination.getTin().equals(currentTin) && tinNpiCombination.getNpi().equals(currentNpi)) {
						isValidCombination = true;
						break;
					}
				}
				if (!isValidCombination) {
					validator.addWarning(Detail.forErrorAndNode(ErrorCode.INCORRECT_API_NPI_COMBINATION, node));
				}

			}
		}
	}

}
