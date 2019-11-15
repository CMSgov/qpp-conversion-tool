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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CpcValidationInfoMap {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(CpcValidationInfoMap.class);
	private Map<String, List<CpcValidationInfo>> apmToSpec;

	public CpcValidationInfoMap(InputStream cpcNpiToApmJson) {
		apmToSpec = convertJsonToMapOfLists(cpcNpiToApmJson);
	}

	private Map<String, CpcValidationInfo> convertJsonToMap(InputStream cpcNpiToApmJson) {
		if (cpcNpiToApmJson == null) {
			return null;
		}
		List<CpcValidationInfo> cpcValidationInfoList = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			cpcValidationInfoList =
				Arrays.asList(objectMapper.readValue(new InputStreamReader(cpcNpiToApmJson, StandardCharsets.UTF_8),
					CpcValidationInfo[].class));
		} catch (IOException exc) {
			DEV_LOG.info("Failed to parse the cpc+ validation npi to apm list...");
		}

		return cpcValidationInfoList.stream()
			.collect(Collectors.toMap(CpcValidationInfo::getTin, Function.identity()));
	}

	private Map<String, CpcValidationInfo> convertJsonToMapOfLists(InputStream cpcNpiToApmJson) {
		if (cpcNpiToApmJson == null) {
			return null;
		}
		List<CpcValidationInfo> cpcValidationInfoList = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			cpcValidationInfoList =
				Arrays.asList(objectMapper.readValue(new InputStreamReader(cpcNpiToApmJson, StandardCharsets.UTF_8),
					CpcValidationInfo[].class));
		} catch (IOException exc) {
			DEV_LOG.info("Failed to parse the cpc+ validation npi to apm list...");
		}

		return cpcValidationInfoList.stream()
			.collect(Collectors.toMap(CpcValidationInfo::getTin, Function.identity()));
	}

	public Map<String, CpcValidationInfo> getApmToSpec() {
		return apmToSpec;
	}
}
