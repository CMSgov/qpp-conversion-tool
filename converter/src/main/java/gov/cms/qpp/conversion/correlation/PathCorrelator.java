package gov.cms.qpp.conversion.correlation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import gov.cms.qpp.conversion.correlation.model.Config;
import gov.cms.qpp.conversion.correlation.model.Correlation;
import gov.cms.qpp.conversion.correlation.model.Goods;
import gov.cms.qpp.conversion.correlation.model.PathCorrelation;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maintains associations between QPP json paths and their pre-transformation xpaths.
 */
public class PathCorrelator {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(PathCorrelator.class);
	public static final String KEY_DELIMITER = "#";
	private static final String ENCODE_LABEL = "encodeLabel";
	private static String config = "pathing/path-correlation.json";
	private static Map<String, Goods> pathCorrelationMap = new HashMap<>();
	private static String uriSubstitution = "";


	static {
		uriSubstitution = loadPathCorrelation().getUriSubstitution();
	}

	private PathCorrelator() {}

	/**
	 * Initializes correlations between json paths and xpaths
	 */
	private static PathCorrelation loadPathCorrelation() {
		PathCorrelation pathCorrelation;

		try {
			InputStream input = ClasspathHelper.contextClassLoader().getResourceAsStream(config);
			ObjectMapper mapper = new ObjectMapper();
			pathCorrelation = mapper.readValue(input, PathCorrelation.class);
			flattenCorrelations(pathCorrelation);
		} catch (IOException ioe) {
			String message = "Problem loading path correlation configuration";
			DEV_LOG.error(message, ioe);
			throw new PathCorrelationException(message, ioe);
		}

		return pathCorrelation;
	}

	/**
	 * Creates a key value store of mappings using the correlation cofiguration:
	 * <a href="https://github.com/CMSgov/qpp-conversion-tool/blob/master/converter/src/main/resources/pathing/path-correlation.json">path-correlation.json</a>
	 *
	 * @param pathCorrelation deserialized representation of the aforementioned correlation configuration
	 */
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
				conf.getEncodeLabels().forEach(label ->
					pathCorrelationMap.put(getKey(template.getTemplateId(), label), conf.getGoods()));
			});
		});
	}

	/**
	 * Retrieve the replacement marker for namespace uri substitution.
	 *
	 * @return substitution place holder
	 */
	static String getUriSubstitution() {
		return uriSubstitution;
	}

	/**
	 * Create key value from given key components.
	 *
	 * @param template template name
	 * @param attribute name encoded or decoded for given template
	 * @return concatenated key value
	 */
	private static String getKey(String template, String attribute) {
		return template + KEY_DELIMITER + attribute;
	}

	/**
	 * Assemble an xpath for a given base template and leaf attribute.
	 *
	 * @param base base template name
	 * @param attribute leaf attribute name
	 * @param uri URI to substitute
	 * @return xpath expression
	 */
	public static String getXpath(String base, String attribute, String uri) {
		String key = PathCorrelator.getKey(base, attribute);
		Goods goods = pathCorrelationMap.get(key);
		return (goods == null) ? null :
				goods.getRelativeXPath().replace(uriSubstitution, uri);
	}

	/**
	 * Assemble an xpath using the given json path and json wrapper.
	 *
	 * @param jsonPath definite json path
	 * @param wrapper object representation of QPP json
	 * @return xpath that correlates to supplied json path
	 */
	public static String prepPath(String jsonPath, JsonWrapper wrapper) {
		String base = "$";
		String leaf = jsonPath;
		int lastIndex = jsonPath.lastIndexOf('.');
		JsonWrapper metaWrapper = new JsonWrapper(wrapper, false);

		if (lastIndex > 0) {
			base = jsonPath.substring(0, lastIndex);
			leaf = jsonPath.substring(lastIndex + 1);
		}

		JsonPath compiledPath = JsonPath.compile(base);
		Map<String, Object> jsonMap = compiledPath.read(metaWrapper.toString());

		Map<String, String> metaMap = getMetaMap(jsonMap, leaf);
		String preparedPath = "";
		if (metaMap != null) {
			preparedPath = makePath(metaMap, leaf);
		}
		return preparedPath;
	}

	/**
	 * Retrieve metadata from map representing a json hash.
	 *
	 * @param jsonMap json hash
	 * @param leaf name of leaf json attribute
	 * @return metadata map
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> getMetaMap(Map<String, Object> jsonMap, final String leaf) {
		List<Map<String, String>> metaHolder = (List<Map<String, String>>) jsonMap.get("metadata_holder");
		return metaHolder.stream()
				.sorted(labeledFirst())
				.filter(entry -> {
					String encodeLabel = entry.get(ENCODE_LABEL);
					if (encodeLabel.equals(leaf)) {
						return leaf.isEmpty()
								|| PathCorrelator.getXpath(entry.get("template"), leaf, entry.get("nsuri")) != null;
					} else {
						return encodeLabel.isEmpty();
					}
				})
				.findFirst()
				.orElse(null);
	}

	/**
	 * Order metadata maps by placing a higher priority on maps containing a non-empty
	 * {@link PathCorrelator#ENCODE_LABEL}.
	 *
	 * @return a comparator that will enact prioritization
	 */
	private static Comparator<Map<String, String>> labeledFirst() {
		return (Map<String, String> map1, Map<String, String> map2) -> {
			String map1Label = map1.get(ENCODE_LABEL);
			String map2Label = map2.get(ENCODE_LABEL);
			
			return Boolean.compare(map1Label.isEmpty(), map2Label.isEmpty());
		};
	}

	/**
	 * Assemble base xpath with a relative xpath that identifies a leaf json attribute.
	 *
	 * @param metadata attribute specific metadata hash
	 * @param leaf attribute name
	 * @return xpath expression
	 */
	private static String makePath(Map<String, String> metadata, final String leaf) {
		String nsUri = metadata.get("nsuri");
		String baseTemplate = metadata.get("template");
		String baseXpath = metadata.get("path");
		String relativeXpath = PathCorrelator.getXpath(baseTemplate, leaf, nsUri);
		return (relativeXpath != null) ? (baseXpath + "/" + relativeXpath) : baseXpath;
	}
}