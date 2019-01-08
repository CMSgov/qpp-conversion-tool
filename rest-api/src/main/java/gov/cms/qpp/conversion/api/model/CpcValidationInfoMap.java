package gov.cms.qpp.conversion.api.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CpcValidationInfoMap {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(CpcValidationInfoMap.class);
	private Map npiToApmMap;

	public CpcValidationInfoMap(InputStream cpcNpiToApmJson){
		npiToApmMap = convertJsonToMap(cpcNpiToApmJson);
	}

	private Map convertJsonToMap(InputStream cpcNpiToApmJson) {
		List<CpcValidationInfo> cpcValidationInfoList = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
			cpcValidationInfoList =
				Arrays.asList(objectMapper.readValue(new InputStreamReader(cpcNpiToApmJson), CpcValidationInfo[].class));
		} catch (IOException exc) {
			DEV_LOG.info("Failed to parse the cpc+ validation npi to apm list...");
		}

		return cpcValidationInfoList.stream()
			.collect(Collectors.toMap(CpcValidationInfo::getApm, CpcValidationInfo::getNpi));
	}

	public Map getNpiToApmMap() {
		return npiToApmMap;
	}
}
