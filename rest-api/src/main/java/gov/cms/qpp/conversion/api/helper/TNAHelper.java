package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.CpcValidationInfo;
import gov.cms.qpp.conversion.api.model.NpiApmWomboCombo;

import java.util.List;

public class TNAHelper {

	private TNAHelper() {
		//empty
	}

	public static boolean checkNpiApmCombinationExistence(CpcValidationInfo newNpiApmCombo,
		List<CpcValidationInfo> validationInfo) {
		boolean results = false;
		for (CpcValidationInfo currentValidationInfo: validationInfo) {
			if (currentValidationInfo.getNpi() == newNpiApmCombo.getNpi() &&
				currentValidationInfo.getApm() == newNpiApmCombo.getApm()) {
				results = true;
				break;
			}
		}
		return results;
	}
}
