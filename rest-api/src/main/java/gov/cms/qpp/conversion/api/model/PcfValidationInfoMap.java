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

	public PcfValidationInfoMap(InputStream pcfApmNpiTinJson) {
		convertJsonToMapOfLists(pcfApmNpiTinJson);
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
			pcfValidationInfoList = Arrays.asList(
					objectMapper.readValue(
							new InputStreamReader(pcfApmNpiTinJson, StandardCharsets.UTF_8),
							PcfValidationInfo[].class
					)
			);
		} catch (IOException exc) {
			DEV_LOG.info("Failed to parse the PCF validation JSON into PcfValidationInfo list.", exc);
		}

		for (PcfValidationInfo pcfValidationInfo : pcfValidationInfoList) {
			if (pcfValidationInfo == null) {
				continue;
			}
			String currentApm = pcfValidationInfo.getApm();
			String currentTin = pcfValidationInfo.getTin();
			String currentNpi = pcfValidationInfo.getNpi();

			if (currentApm == null || currentTin == null || currentNpi == null) {
				continue;
			}

			apmTinNpiCombinationMap
					.computeIfAbsent(currentApm, k -> new HashMap<>())
					.computeIfAbsent(currentTin, k -> new ArrayList<>());

			List<String> npiList = apmTinNpiCombinationMap.get(currentApm).get(currentTin);
			if (!npiList.contains(currentNpi)) {
				npiList.add(currentNpi);
			}
		}
	}

	/**
	 * Returns a defensive deep copy of the internal map structure.
	 */
	public Map<String, Map<String, List<String>>> getApmTinNpiCombinationMap() {
		if (apmTinNpiCombinationMap == null) {
			return null;
		}
		Map<String, Map<String, List<String>>> topLevelCopy = new HashMap<>();
		for (Map.Entry<String, Map<String, List<String>>> apmEntry : apmTinNpiCombinationMap.entrySet()) {
			String apmKey = apmEntry.getKey();
			Map<String, List<String>> tinMap = apmEntry.getValue();
			Map<String, List<String>> tinMapCopy = new HashMap<>();
			for (Map.Entry<String, List<String>> tinEntry : tinMap.entrySet()) {
				String tinKey = tinEntry.getKey();
				List<String> npiList = tinEntry.getValue();
				tinMapCopy.put(tinKey, new ArrayList<>(npiList));
			}
			topLevelCopy.put(apmKey, tinMapCopy);
		}
		return topLevelCopy;
	}
}
