package gov.cms.qpp.conversion.api.internal.pii;

import org.springframework.util.StringUtils;

import gov.cms.qpp.conversion.api.model.CpcValidationInfoMap;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
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
		String program = node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME);
		String apm = getApmEntityId(node, program);
		List<String> npiList = Arrays.asList(
			node.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER).split(","));
		List<String> tinList = Arrays.asList(
			node.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER).split(","));

		Map<String, Map<String, List<String>>> apmToTinNpiMap = file.getApmTinNpiCombinationMap();
		if (apmToTinNpiMap == null || StringUtils.isEmpty(apm)) {
			validator.addWarning(Detail.forProblemAndNode(ProblemCode.MISSING_API_TIN_NPI_FILE, node));
		} else {
			Map<String, List<String>> tinNpisMap = apmToTinNpiMap.get(apm);
			int npiSize = npiList.size();
			for (int index = 0; index < npiSize; index++) {
				String currentTin = tinList.get(index).trim();
				String currentNpi = npiList.get(index).trim();
				String maskedTin = "*****" + currentTin.substring(5);
				LocalizedProblem error = ProblemCode.INCORRECT_API_NPI_COMBINATION
					.format(currentNpi, maskedTin, apm);
				if (tinNpisMap == null || tinNpisMap.get(currentTin) == null
					|| !(tinNpisMap.get(currentTin).indexOf(currentNpi) > -1)) {
					validator.addWarning(Detail.forProblemAndNode(error, node));
				}
			}
		}
	}

	private String getApmEntityId(final Node node, final String program) {
		String apm;
		if (ClinicalDocumentDecoder.PCF_PROGRAM_NAME.equalsIgnoreCase(program)) {
			apm = node.getValue(ClinicalDocumentDecoder.PCF_ENTITY_ID);
		} else if (ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME.equalsIgnoreCase(program)) {
			apm = node.getValue(ClinicalDocumentDecoder.PRACTICE_ID);
		} else {
			apm = "";
		}
		return apm;
	}

}
