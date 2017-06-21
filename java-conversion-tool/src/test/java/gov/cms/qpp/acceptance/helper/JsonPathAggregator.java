package gov.cms.qpp.acceptance.helper;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.decode.QualitySectionDecoder;
import gov.cms.qpp.conversion.encode.AciSectionEncoder;
import gov.cms.qpp.conversion.encode.QualityMeasureIdEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonPathAggregator {
	private Set<String> excluded = Sets.newHashSet(
			QualityMeasureIdEncoder.IS_END_TO_END_REPORTED,
			QualitySectionDecoder.CATEGORY,
			AciSectionEncoder.SUBMISSION_METHOD
	);

	Map<String, String> jsonPaths = new HashMap<>();

	public JsonPathAggregator(JsonNode node) {
		aggregatePaths("$", node);
	}

	private void aggregatePaths(String context, JsonNode node) {
		JsonNodeType type = node.getNodeType();
		//disregard JsonNodeType.MISSING for the moment
		if (node.isArray()) {
			int index = 0;
			for(JsonNode child : node) {
				String newContext = context + "[" + index++ + "]";
				aggregatePaths(newContext, child);
			}
		} else if (node.isObject()) {
			node.fieldNames().forEachRemaining( name -> {
				if (excluded.contains(name)) {
					return;
				}
				String newContext = context + "." + name;
				aggregatePaths(newContext, node.get(name));
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
