package gov.cms.qpp.acceptance.helper;

import gov.cms.qpp.conversion.decode.SkeletalSectionDecoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.model.Constants;

import static gov.cms.qpp.conversion.model.Constants.SUBMISSION_METHOD;

public class JsonPathAggregator {
	private Set<String> excluded = Sets.newHashSet(
			Constants.IS_END_TO_END_REPORTED,
			Constants.CATEGORY,
			SUBMISSION_METHOD
	);

	Map<String, String> jsonPaths = new HashMap<>();

	public JsonPathAggregator(JsonNode node) {
		aggregatePaths("$", node);
	}

	private void aggregatePaths(String context, JsonNode node) {
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
