package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.helper.QualityMeasuresLookup;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Encoder to serialize Quality Measure Identifier
 */
@Encoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdEncoder extends QppOutputEncoder {

	private static final String MEASURE_ID = "measureId";
	private static final String AGGREGATE_COUNT = "aggregateCount";
	private static final String TYPE = "type";

	/**
	 * Encodes an Quality Measure Id into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the Quality Measure Identifier
	 * @param node Node that represents the Quality Measure Identifier
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		Map<String, MeasureConfig> configurationMap = MeasureConfigs.getConfigurationMap();
		String measureId = QualityMeasuresLookup.getMeasureId(node.getValue(MEASURE_ID));
		MeasureConfig measureConfig = configurationMap.get(node.getValue(MEASURE_ID));

		if (isASinglePerformanceRate(measureConfig, measureId)) {
			wrapper.putString(MEASURE_ID, measureId);
			encodeChildren(wrapper, node);
		} else {
			encodeMultiPerformanceRate(node, wrapper, measureConfig);
		}
	}

	private boolean isASinglePerformanceRate(MeasureConfig measureConfig, String measureId) {
		if (measureConfig == null) {
			Converter.CLIENT_LOG.info("Measure Configuration for {} is missing", measureId);
			return true;
		}
		return "singlePerformanceRate".equalsIgnoreCase(measureConfig.getMetricType());
	}

	/**
	 * Encodes a multi performance rate proportion measure
	 *
	 * @param node parent node that holds the current performance rate proportion measures
	 * @param wrapper object to be encoded into
	 * @param measureConfig configurations to group performance rate proportion measures
	 */
	private void encodeMultiPerformanceRate(Node node, JsonWrapper wrapper, MeasureConfig measureConfig) {
		List<Node> subPopNodes = createSubPopulationGrouping(node, measureConfig);
		encodeMultiPerformanceChildren(wrapper, subPopNodes);
	}

	/**
	 * Creates a grouping of sub populations extracted from the measure configurations
	 *
	 * @param node object that holds the nodes to be grouped
	 * @param measureConfig object that holds the groupings
	 * @return
	 */
	private List<Node> createSubPopulationGrouping(Node node, MeasureConfig measureConfig) {
		int subPopCount = measureConfig.getSubPopulation().size();
		List<Node> subPopNodes = new ArrayList<>(subPopCount);
		Map<String, Integer> mapPopulationIdToSubPopIndex = createSubPopulationIndexMap(measureConfig);
		for (Node childNode : node.getChildNodes()) {
			String populationId = childNode.getValue("populationId");

			int subPopIndex = mapPopulationIdToSubPopIndex.get(populationId);
			Node newParentNode = subPopNodes.get(subPopIndex);
			if (newParentNode == null) {
				newParentNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId());
				subPopNodes.set(subPopIndex, newParentNode);
			}
			newParentNode.addChildNode(childNode);
		}
		return subPopNodes;
	}

	/**
	 * Creates a map of child guids to indexes for sub population grouping
	 *
	 * @param measureConfig configurations that group the sub populations
	 * @return
	 */
	private Map<String, Integer> createSubPopulationIndexMap(MeasureConfig measureConfig) {
		Map<String, Integer> supPopMap = new HashMap<>();
		int index = 0;
		for (SubPopulation subPopulation : measureConfig.getSubPopulation()) {
			supPopMap.put(subPopulation.getDenominatorUuid(), index);
			supPopMap.put(subPopulation.getDenominatorExceptionsUuid(), index);
			supPopMap.put(subPopulation.getDenominatorExclusionsUuid(), index);
			supPopMap.put(subPopulation.getNumeratorUuid(), index);
			supPopMap.put(subPopulation.getInitialPopulationUuid(), index);
			index++;
		}
		return supPopMap;
	}

	/**
	 * Encode child nodes.
	 *
	 * @param wrapper holder for encoded node data
	 * @param parentNode holder of the Quality Measures
	 */
	private void encodeChildren(JsonWrapper wrapper, Node parentNode) {
		JsonWrapper childWrapper = new JsonWrapper();

		childWrapper.putBoolean("isEndToEndReported", "true");

		encodeSubPopulation(parentNode, childWrapper);

		wrapper.putObject("value", childWrapper);
	}

	/**
	 * Encode multi performance child nodes
	 *
	 * @param wrapper holder for encoded node data
	 * @param subPopNodes holder of the Quality Measures
	 */
	private void encodeMultiPerformanceChildren(JsonWrapper wrapper, List<Node> subPopNodes) {
		JsonWrapper childWrapper = new JsonWrapper();

		childWrapper.putBoolean("isEndToEndReported", "true");

		JsonWrapper strataListWrapper = new JsonWrapper();
		for (Node subPopNode : subPopNodes) {
			JsonWrapper strataWrapper = new JsonWrapper();
			encodeSubPopulation(subPopNode, strataWrapper);
			strataListWrapper.putObject(strataWrapper);
		}
		childWrapper.putObject("strata", strataListWrapper);

		wrapper.putObject("value", childWrapper);
	}

	/**
	 * Encodes a sub population
	 *
	 * @param parentNode holder of the sub populations
	 * @param childWrapper holder of encoded sub populations
	 */
	private void encodeSubPopulation(Node parentNode, JsonWrapper childWrapper) {
		this.encodePopulationTotal(childWrapper, parentNode);
		this.encodePerformanceMet(childWrapper, parentNode);
		this.encodePerformanceNotMet(childWrapper, parentNode);

		for (Node childNode : parentNode.getChildNodes()) {
			JsonOutputEncoder measureDataEncoder = ENCODERS.get(childNode.getId());
			measureDataEncoder.encode(childWrapper, childNode);
		}
	}

	/**
	 * Encodes a population total from a initial population node
	 *
	 * @param wrapper holder of the encoded initial population
	 * @param parentNode holder of the initial population
	 */
	private void encodePopulationTotal(JsonWrapper wrapper, Node parentNode) {
		Set<String> accepted = new HashSet(Arrays.asList("IPOP", "IPP"));
		Node populationNode = parentNode.findChildNode(n -> accepted.contains(n.getValue(TYPE)));

		Optional.ofNullable(populationNode).ifPresent(
				node -> wrapper.putInteger("populationTotal",
						node.getChildNodes().get(0).getValue(AGGREGATE_COUNT)));
	}

	/**
	 * Encodes a performance met from a numerator node
	 *
	 * @param wrapper holder of the encoded numerator node
	 * @param parentNode holder of the the numerator node
	 */
	private void encodePerformanceMet(JsonWrapper wrapper, Node parentNode) {
		Node numeratorNode = parentNode.findChildNode(n -> "NUMER".equals(n.getValue(TYPE)));

		Optional.ofNullable(numeratorNode).ifPresent(
				node -> wrapper.putInteger("performanceMet",
						node.getChildNodes().get(0).getValue(AGGREGATE_COUNT)));
	}

	/**
	 * Encodes a performance not met from denominator and denominator exclusion
	 *
	 * @param wrapper holder of the encoded denominator and denominator exclusion nodes
	 * @param parentNode holder of the denominator and denominator exclusion nodes
	 */
	private void encodePerformanceNotMet(JsonWrapper wrapper, Node parentNode) {
		Node denomExclusionNode = parentNode.findChildNode(n -> "DENEX".equals(n.getValue(TYPE)));
		Node denominatorNode = parentNode.findChildNode(n -> "DENOM".equals(n.getValue(TYPE)));

		Optional.ofNullable(denomExclusionNode).ifPresent(
				node -> wrapper.putInteger("performanceExclusion",
						node.getChildNodes().get(0).getValue(AGGREGATE_COUNT)));

		Optional.ofNullable(calculatePerformanceNotMet(denominatorNode, denomExclusionNode)).ifPresent(
				performanceNotMet -> wrapper.putInteger("performanceNotMet", performanceNotMet));
	}

	/**
	 * Calculates performance not met
	 *
	 * @param denominatorNode holder of the denominator aggregate count value
	 * @param denomExclusionNode holder of the denominator exclusion aggregate count value
	 * @return the calculation
	 */
	private String calculatePerformanceNotMet(Node denominatorNode, Node denomExclusionNode) {
		if (null == denominatorNode || null == denomExclusionNode) {
			return null;
		}
		String denominatorValue = denominatorNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);
		String denomExclusionValue = denomExclusionNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);

		return Integer.toString(Integer.parseInt(denominatorValue) - Integer.parseInt(denomExclusionValue));
	}
}
