package gov.cms.qpp.conversion.api.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.api.helper.TNAHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CpcValidationInfoMap {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(CpcValidationInfoMap.class);
	private HashMap<String, List<CpcValidationInfo>> apmTinNpiCombinations;

	public CpcValidationInfoMap(InputStream cpcNpiToApmJson) {
		apmTinNpiCombinations = convertJsonToMapOfLists(cpcNpiToApmJson);
	}

	private HashMap<String, List<CpcValidationInfo>> convertJsonToMapOfLists(InputStream cpcApmNpiTinJson) {
		if (cpcApmNpiTinJson == null) {
			return null;
		}
		List<CpcValidationInfo> cpcValidationInfoList = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			cpcValidationInfoList =
				Arrays.asList(objectMapper.readValue(new InputStreamReader(cpcApmNpiTinJson, StandardCharsets.UTF_8),
					CpcValidationInfo[].class));
		} catch (IOException exc) {
			DEV_LOG.info("Failed to parse the cpc+ validation npi to apm list...");
		}

		HashMap<String, List<CpcValidationInfo>> apmTinNpiMap = new HashMap<>();
		for (CpcValidationInfo cpcValidationInfo: cpcValidationInfoList) {
			if (apmTinNpiMap.containsKey(cpcValidationInfo.getApm())) {
				if(!TNAHelper.tinNpiCombinationExists(cpcValidationInfo, apmTinNpiMap.get(cpcValidationInfo.getApm()))) {
					apmTinNpiMap.get(cpcValidationInfo.getApm()).add(cpcValidationInfo);
				}
			} else {
				ArrayList<CpcValidationInfo> validationInfo = new ArrayList<>();
				validationInfo.add(cpcValidationInfo);
				apmTinNpiMap.put(cpcValidationInfo.getApm(), validationInfo);
			}
		}

		return apmTinNpiMap;
	}

	public Map<String, List<CpcValidationInfo>> getApmTinNpiCombination() {
		return apmTinNpiCombinations;
	}
}
