package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.encode.helper.QualityMeasuresLookup;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Encoder to serialize Quality Measure Identifier and Measure Sections
 */
@Encoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdEncoder extends QppOutputEncoder {

	private static final String MEASURE_ID = "measureId";
	private static final String AGGREGATE_COUNT = "aggregateCount";
	private static final String TYPE = "type";
	private static final String SINGLE_PERFORMANCE_RATE = "singlePerformanceRate";
	public static final String IS_END_TO_END_REPORTED = "isEndToEndReported";
	private static final String TRUE = "true";
	private boolean multiPerformanceRate = false; //Decides if encoded should include a stratum attribute

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
			multiPerformanceRate = false;
			wrapper.putString(MEASURE_ID, measureId);
			encodeChildren(wrapper, node);
		} else {
			multiPerformanceRate = true;
			wrapper.putString(MEASURE_ID, measureId);
			encodeMultiPerformanceRate(wrapper, node, measureConfig);
		}
	}

	/**
	 * Checks if is a single performance rate.
	 * Defaults to single performance rate for missing configuration mappings
	 *
	 * @param measureConfig configuration in check
	 * @param measureId variable to show when the mapping is non existent
	 * @return
	 */
	private boolean isASinglePerformanceRate(MeasureConfig measureConfig, String measureId) {
		if (measureConfig == null) {
			Converter.CLIENT_LOG.info("Measure Configuration for {} is missing", measureId);
			return true;
		}
		return SINGLE_PERFORMANCE_RATE.equalsIgnoreCase(measureConfig.getMetricType());
	}

	/**
	 * Encode child nodes.
	 *
	 * @param wrapper holder for encoded node data
	 * @param parentNode holder of the Quality Measures
	 */
	private void encodeChildren(JsonWrapper wrapper, Node parentNode) {
		JsonWrapper childWrapper = new JsonWrapper();
		childWrapper.putBoolean(IS_END_TO_END_REPORTED, TRUE);
		encodeSubPopulation(parentNode, childWrapper);
		wrapper.putObject(VALUE, childWrapper);
	}

	/**
	 * Encodes a multi performance rate proportion measure
	 *
	 * @param wrapper object to be encoded into
	 * @param node parent node that holds the current performance rate proportion measures
	 * @param measureConfig configurations to group performance rate proportion measures
	 */
	private void encodeMultiPerformanceRate(JsonWrapper wrapper, Node node, MeasureConfig measureConfig) {
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
		List<Node> subPopNodes = initializeMeasureDataList(subPopCount);
		Map<String, Integer> mapPopulationIdToSubPopIndex = createSubPopulationIndexMap(measureConfig);
		node.getChildNodes().stream()
				.filter(childNode -> TemplateId.MEASURE_DATA_CMS_V2 == childNode.getType())
				.forEach(childNode -> {
					String populationId = childNode.getValue(MeasureDataDecoder.MEASURE_POPULATION);
					Integer subPopIndex = mapPopulationIdToSubPopIndex.get(populationId);
					if (subPopIndex != null) {
						Node newParentNode = subPopNodes.get(subPopIndex);
						newParentNode.addChildNode(childNode);
					}
				});
		return subPopNodes;
	}

	/**
	 * Initializes a list of Measure Section nodes from how many sub populations are being converted
	 *
	 * @param subPopulationCount number of sub populations to convert
	 * @return
	 */
	private List<Node> initializeMeasureDataList(int subPopulationCount) {
		return IntStream.range(0, subPopulationCount)
				.mapToObj(ignore -> new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2))
				.collect(Collectors.toList());
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
	 * Encode multi performance child nodes
	 *
	 * @param wrapper holder for encoded node data
	 * @param subPopNodes holder of the Quality Measures
	 */
	private void encodeMultiPerformanceChildren(JsonWrapper wrapper, List<Node> subPopNodes) {
		JsonWrapper childWrapper = new JsonWrapper();

		childWrapper.putBoolean(IS_END_TO_END_REPORTED, TRUE);

		JsonWrapper strataListWrapper = new JsonWrapper();
		for (Node subPopNode : subPopNodes) {
			JsonWrapper strataWrapper = new JsonWrapper();
			encodeSubPopulation(subPopNode, strataWrapper);
			strataListWrapper.putObject(strataWrapper);
		}
		childWrapper.putObject("strata", strataListWrapper);

		wrapper.putObject(VALUE, childWrapper);
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
			JsonOutputEncoder measureDataEncoder = ENCODERS.get(childNode.getType());
			measureDataEncoder.encode(childWrapper, childNode);
		}
		this.encodeStratum(childWrapper, parentNode);
	}

	/**
	 * Encodes a population total from a initial population node
	 *
	 * @param wrapper holder of the encoded initial population
	 * @param parentNode holder of the initial population
	 */
	private void encodePopulationTotal(JsonWrapper wrapper, Node parentNode) {
		Set<String> accepted = new HashSet<>(Arrays.asList("IPOP", "IPP"));
		Node populationNode = parentNode.findChildNode(n -> accepted.contains(n.getValue(TYPE)));

		Optional.ofNullable(populationNode).ifPresent(
			node -> {
				Node aggCount = node.getChildNodes().get(0);
				maintainContinuity(wrapper, aggCount, "populationTotal");
				wrapper.putInteger("populationTotal", aggCount.getValue(AGGREGATE_COUNT));
			}
		);
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
			node -> {
				Node aggCount = node.getChildNodes().get(0);
				maintainContinuity(wrapper, aggCount, "performanceMet");
				wrapper.putInteger("performanceMet", aggCount.getValue(AGGREGATE_COUNT));
			});
	}

	/**
	 * Adds the Stratum attribute to the QPP document
	 * @param wrapper JsonWrapper
	 * @param parentNode Node
	 */
	private void encodeStratum(JsonWrapper wrapper, Node parentNode) {
		if (!multiPerformanceRate) {
			return;
		}
		Node numeratorNode = parentNode.findChildNode(n -> "NUMER".equals(n.getValue(TYPE)));

		Optional.ofNullable(numeratorNode).ifPresent(
			node -> {
				maintainContinuity(wrapper, node, "stratum");
				wrapper.putString("stratum", node.getValue(MeasureDataDecoder.MEASURE_POPULATION));
			});
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
				node -> {
					Node aggCount = node.getChildNodes().get(0);
					maintainContinuity(wrapper, aggCount, "performanceExclusion");
					wrapper.putInteger("performanceExclusion", aggCount.getValue(AGGREGATE_COUNT));
				});

		Optional.ofNullable(calculatePerformanceNotMet(denominatorNode, denomExclusionNode)).ifPresent(
				performanceNotMet -> {
					//have to choose one of denominatorNode, denomExclusionNode
					//Why are we deriving values???
					maintainContinuity(wrapper, denomExclusionNode, "performanceNotMet");
					wrapper.putInteger("performanceNotMet", performanceNotMet);
				});
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
