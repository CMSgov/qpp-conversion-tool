package gov.cms.qpp.conversion.correlation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import gov.cms.qpp.conversion.correlation.model.Config;
import gov.cms.qpp.conversion.correlation.model.Correlation;
import gov.cms.qpp.conversion.correlation.model.Goods;
import gov.cms.qpp.conversion.correlation.model.PathCorrelation;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.reflections.util.ClasspathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathCorrelator {
	private static final String ENCODE_LABEL = "encodeLabel";
	private static String config = "pathing/path-correlation.json";
	private static PathCorrelation pathCorrelation;
	private static Map<String, Goods> pathCorrelationMap = new HashMap<>();

	static {
		initPathCorrelation();
	}

	private PathCorrelator() {}

	private static void initPathCorrelation() {
		try {
			InputStream input = ClasspathHelper.contextClassLoader().getResourceAsStream(config);
			ObjectMapper mapper = new ObjectMapper();
			pathCorrelation = mapper.readValue(input, PathCorrelation.class);
			flattenCorrelations(pathCorrelation);
		} catch (IOException ioe) {
			throw new PathCorrelationException("Problem loading path correlation configuration", ioe);
		}
	}

	private static void flattenCorrelations(PathCorrelation pathCorrelation) {
		Map<String, List<Config>> config = pathCorrelation.getCorrelations().stream()
				.collect(Collectors.toMap(Correlation::getCorrelationId, Correlation::getConfig));
		pathCorrelation.getTemplates().forEach(template -> {
			List<Config> configs = config.get(template.getCorrelationId());
			configs.forEach(conf -> {
				if (null != conf.getDecodeLabel()) {
					pathCorrelationMap.put(
							getKey(template.getTemplateId(), conf.getDecodeLabel()), conf.getGoods());
				}
				if (null != conf.getEncodeLabels()) {
					conf.getEncodeLabels().forEach(label ->
						pathCorrelationMap.put(getKey(template.getTemplateId(), label), conf.getGoods()));
				}
			});
		});
	}

	static String getUriSubstitution() {
		return pathCorrelation.getUriSubstitution();
	}

	public static String getKey(String template, String attribute) {
		return template + "_" + attribute;
	}

	public static String getXpath(String base, String attribute, String uri) {
		String key = PathCorrelator.getKey(base, attribute);
		Goods goods = pathCorrelationMap.get(key);
		return (goods == null) ? null :
				goods.getRelativeXPath().replaceAll(pathCorrelation.getUriSubstitution(), uri);
	}

	@SuppressWarnings("unchecked")
	public static String prepPath(String jsonPath, JsonWrapper wrapper) {
		String base = "$";
		String leaf = jsonPath;
		int lastIndex = jsonPath.lastIndexOf('.');

		if (lastIndex > 0) {
			base = jsonPath.substring(0, lastIndex);
			leaf = jsonPath.substring(lastIndex + 1);
		}

		JsonPath compiledPath = JsonPath.compile(base);
		Map<String, Object> jsonMap = compiledPath.read(wrapper.toString());
		Map<String, String> metaMap = getMetaMap(jsonMap, leaf);
		return makePath(metaMap, leaf);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> getMetaMap(Map<String, Object> jsonMap, final String leaf) {
		List<Map<String, String>> metaHolder = (List<Map<String, String>>) jsonMap.get("metadata_holder");
		Stream<Map<String, String>> sorted = metaHolder.stream()
				.sorted(labeledFirst());
		return sorted.filter(entry -> {
			if (entry.get(ENCODE_LABEL).equals(leaf)) {
				return leaf.isEmpty()
						|| PathCorrelator.getXpath(entry.get("template"), leaf, entry.get("nsuri")) != null;
			} else {
				return entry.get(ENCODE_LABEL).isEmpty();
			}
		}).findFirst().orElse(null);
	}

	private static Comparator<Map<String, String>> labeledFirst() {
		return (Map<String, String> map1, Map<String, String> map2) -> {
			String map1Label = map1.get(ENCODE_LABEL);
			String map2Label = map2.get(ENCODE_LABEL);
			int reply;
			if ((!map1Label.isEmpty() && !map2Label.isEmpty())
					|| (map1Label.isEmpty() && map2Label.isEmpty())) {
				reply = 0;
			} else if (map1Label.isEmpty()) {
				reply = 1;
			} else {
				reply = -1;
			}
			return reply;
		};
	}

	private static String makePath(Map<String, String> metadata, final String leaf) {
		String nsUri = metadata.get("nsuri");
		String baseTemplate = metadata.get("template");
		String baseXpath = metadata.get("path");
		String relativeXpath = PathCorrelator.getXpath(baseTemplate, leaf, nsUri);
		return (relativeXpath != null) ? baseXpath + "/" + relativeXpath : baseXpath;
	}


}
