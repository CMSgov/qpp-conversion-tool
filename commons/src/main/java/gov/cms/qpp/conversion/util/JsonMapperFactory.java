package gov.cms.qpp.conversion.util;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public final class JsonMapperFactory {
	private JsonMapperFactory() {
	}

	public static ObjectMapper createMapper() {
		return JsonMapper.builder()
				.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
				.build();
	}
}
