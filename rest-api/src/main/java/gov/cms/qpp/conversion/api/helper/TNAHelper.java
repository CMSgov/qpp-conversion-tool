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
			if (currentValidationInfo.getTin().equals(newNpiApmCombo.getTin()) &&
				currentValidationInfo.getApm().equals(newNpiApmCombo.getApm())) {
				return true;
			}
		}
		return results;
	}
}
