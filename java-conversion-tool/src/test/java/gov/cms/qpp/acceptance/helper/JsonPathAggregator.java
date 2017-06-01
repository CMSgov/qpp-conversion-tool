package gov.cms.qpp.acceptance.helper;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import gov.cms.qpp.conversion.decode.QualitySectionDecoder;
import gov.cms.qpp.conversion.encode.QualityMeasureIdEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonPathAggregator {
	private List<String> excluded = Arrays.asList(
			QualityMeasureIdEncoder.IS_END_TO_END_REPORTED,
			QualitySectionDecoder.CATEGORY,
			QualitySectionDecoder.SUBMISSION_METHOD
	);

	Map<String, String> jsonPaths = new HashMap<>();

	public JsonPathAggregator(JsonNode node) {
		checkPaths("$", node);
	}

	private void checkPaths(String context, JsonNode node) {
		JsonNodeType type = node.getNodeType();
		//disregard JsonNodeType.MISSING for the moment
		if (node.isArray()) {
			int index = 0;
			for(JsonNode child : node) {
				String newContext = context + "[" + index++ + "]";
				checkPaths(newContext, child);
			}
		} else if (node.isObject()) {
			node.fieldNames().forEachRemaining( name -> {
				if (excluded.contains(name)) {
					return;
				}
				String newContext = context + "." + name;
				checkPaths(newContext, node.get(name));
			});
		} else if (node.isValueNode()) {
			String value = node.asText();
			jsonPaths.put(context, value);
		}
	}

	public Map<String, String> getJsonPaths() {
		return jsonPaths;
	}
}
