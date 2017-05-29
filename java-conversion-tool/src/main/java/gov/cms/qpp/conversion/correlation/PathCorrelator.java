package gov.cms.qpp.conversion.correlation;


import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.correlation.model.Goods;
import gov.cms.qpp.conversion.correlation.model.PathCorrelation;
import gov.cms.qpp.conversion.correlation.model.PathCorrelations;
import org.reflections.util.ClasspathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PathCorrelator {
	private static PathCorrelations pathCorrelations;
	private static Map<String, Goods> pathCorrelationMap = new HashMap<>();

	static {
		try {
			InputStream input = ClasspathHelper.contextClassLoader()
					.getResourceAsStream("pathing/path-correlation.json");
			ObjectMapper mapper = new ObjectMapper();
			pathCorrelations = mapper.readValue(input, PathCorrelations.class);
			flattenCorrelations(pathCorrelations);
		} catch(IOException ioe) {
			throw new PathCorrelationException("Problem loading path correlation configuration", ioe);
		}
	}

	private static void flattenCorrelations(PathCorrelations pathCorrelations) {
		pathCorrelations.getPathCorrelations().stream()
				.collect(Collectors.toMap(PathCorrelation::getTemplateId, pc -> pc))
				.entrySet().stream().forEach(PathCorrelator::loadFlattenedEntries);
	}

	private static void loadFlattenedEntries(Map.Entry<String, PathCorrelation> entry) {
		String key = entry.getKey();
		PathCorrelation value = entry.getValue();
		value.getCorrelations().stream().forEach(correlation -> {
			if (null != correlation.getDecodeLabel()) {
				pathCorrelationMap.put(
						getKey(key, correlation.getDecodeLabel()), correlation.getGoods());
			}
			if (null != correlation.getEncodeLabel()) {
				pathCorrelationMap.put(
						getKey(key, correlation.getEncodeLabel()), correlation.getGoods());
			}
		});
	}

	public static String getKey(String template, String attribute) {
		return template + "_" + attribute;
	}

	public static String getPath(String key, String uri) {
		Goods goods = pathCorrelationMap.get(key);
		return (goods == null) ? null :
				goods.getRelativeXPath().replaceAll(pathCorrelations.getUriSubstitution(), uri);
	}
}
