package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.CpcValidationInfo;

import java.util.List;

public class TNAHelper {

	private TNAHelper() {
		//empty
	}

	public static boolean tinNpiCombinationExists(CpcValidationInfo newNpiApmCombo,
		List<CpcValidationInfo> validationInfo) {
		boolean results = false;
		for (CpcValidationInfo currentValidationInfo: validationInfo) {
			if (currentValidationInfo.getTin() == newNpiApmCombo.getTin() &&
				currentValidationInfo.getApm() == newNpiApmCombo.getApm()) {
				results = true;
				break;
			}
		}
		return results;
	}
}
