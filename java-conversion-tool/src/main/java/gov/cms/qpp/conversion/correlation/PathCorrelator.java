package gov.cms.qpp.conversion.correlation;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.correlation.model.Config;
import gov.cms.qpp.conversion.correlation.model.Correlation;
import gov.cms.qpp.conversion.correlation.model.Goods;
import gov.cms.qpp.conversion.correlation.model.PathCorrelation;
import org.reflections.util.ClasspathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PathCorrelator {
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
}
