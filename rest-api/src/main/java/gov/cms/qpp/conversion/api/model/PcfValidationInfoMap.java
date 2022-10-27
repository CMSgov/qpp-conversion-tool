package gov.cms.qpp.conversion.api.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PcfValidationInfoMap {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(PcfValidationInfoMap.class);
	private Map<String, Map<String, List<String>>> apmTinNpiCombinationMap;

	public PcfValidationInfoMap(InputStream pcfNpiToApmJson) {
		convertJsonToMapOfLists(pcfNpiToApmJson);
	}

	private void convertJsonToMapOfLists(InputStream pcfApmNpiTinJson) {
		if (pcfApmNpiTinJson == null) {
			apmTinNpiCombinationMap = null;
			return;
		}

		apmTinNpiCombinationMap = new HashMap<>();

		List<PcfValidationInfo> pcfValidationInfoList = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			pcfValidationInfoList =
				Arrays.asList(objectMapper.readValue(new InputStreamReader(pcfApmNpiTinJson, StandardCharsets.UTF_8),
					PcfValidationInfo[].class));
		} catch (IOException | NullPointerException exc){
			DEV_LOG.info("Failed to parse the pcf validation npi to apm list...");
		}

		for (PcfValidationInfo pcfValidationInfo : pcfValidationInfoList) {
			String currentApm = pcfValidationInfo.getApm();
			String currentTin = pcfValidationInfo.getTin();
			String currentNpi = pcfValidationInfo.getNpi();

			if(apmTinNpiCombinationMap.containsKey(currentApm)) {
				if (!hasTinKey(currentApm, currentTin)) {
					List<String> npiList = new ArrayList<>();
					npiList.add(currentNpi);
					apmTinNpiCombinationMap.get(currentApm).put(currentTin, npiList);
				} else if(!isExistingCombination(currentApm, currentTin, pcfValidationInfo.getNpi())) {
					apmTinNpiCombinationMap.get(currentApm).get(currentTin).add(currentNpi);
				}
			} else {
				Map<String, List<String>> tinNpisMap = new HashMap<>();
				List<String> npiList = new ArrayList<>();
				npiList.add(currentNpi);
				tinNpisMap.put(currentTin, npiList);
				apmTinNpiCombinationMap.put(currentApm, tinNpisMap);
			}
		}
	}

	private boolean hasTinKey(String apm, String tin) {
		return (apmTinNpiCombinationMap.get(apm).containsKey(tin));
	}

	private boolean isExistingCombination(String apm, String tin, String npi) {
		return (apmTinNpiCombinationMap.get(apm).get(tin)).indexOf(npi) > -1;
	}

	public Map<String, Map<String, List<String>>> getApmTinNpiCombinationMap() {
		return apmTinNpiCombinationMap;
	}
}
