package gov.cms.qpp.conversion.api.internal.pii;

import gov.cms.qpp.conversion.api.model.CpcValidationInfoMap;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.pii.PiiValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

		Map<String, List<String>> tinNpisMap = file.getApmTinNpiCombinationMap().get(apm);
		if (tinNpisMap == null) {
			validator.addWarning(Detail.forErrorAndNode(ErrorCode.MISSING_API_TIN_NPI_FILE, node));
		} else {
			int npiSize = npiList.size();
			for (int index = 0; index < npiSize; index++) {
				String currentTin = tinList.get(index).trim();
				String currentNpi = npiList.get(index).trim();
				String maskedTin = "*****" + currentTin.substring(5);
				LocalizedError error = ErrorCode.INCORRECT_API_NPI_COMBINATION
					.format(currentNpi, maskedTin, apm);
				if (tinNpisMap.get(currentTin) == null || !(tinNpisMap.get(currentTin).indexOf(currentNpi) > -1)) {
					validator.addWarning(Detail.forErrorAndNode(error, node));
				}
			}
		}
	}

}
