package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.Strata;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	public QualityMeasureIdEncoder(Context context) {
		super(context);
	}

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
		MeasureConfig measureConfig = configurationMap.get(node.getValue(MEASURE_ID));
		String measureId = measureConfig.getMeasureId();
		wrapper.putString(MEASURE_ID, measureId);

		if (isASinglePerformanceRate(measureConfig)) {
			encodeChildren(wrapper, node, measureConfig);
		} else {
			encodeMultiPerformanceRate(wrapper, node, measureConfig);
		}
	}

	/**
	 * Checks if is a single performance rate.
	 * Defaults to single performance rate for missing configuration mappings
	 *
	 * @param measureConfig configuration in check
	 * @return SINGLE_PERFORMANCE_RATE == measureConfig.getMetricType()
	 */
	private boolean isASinglePerformanceRate(MeasureConfig measureConfig) {
		return SINGLE_PERFORMANCE_RATE.equalsIgnoreCase(measureConfig.getMetricType());
	}

	/**
	 * Encode child nodes.
	 * @param wrapper holder for encoded node data
	 * @param parentNode holder of the Quality Measures
	 * @param measureConfig The measure configuration for the current measure.
	 */
	private void encodeChildren(JsonWrapper wrapper, Node parentNode, final MeasureConfig measureConfig) {
		JsonWrapper childWrapper = new JsonWrapper();
		childWrapper.putBoolean(IS_END_TO_END_REPORTED, TRUE);
		encodeSubPopulation(parentNode, childWrapper, false, measureConfig);
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
		encodeMultiPerformanceChildren(wrapper, subPopNodes, measureConfig);
	}

	/**
	 * Creates a grouping of sub populations extracted from the measure configurations
	 *
	 * @param node object that holds the nodes to be grouped
	 * @param measureConfig object that holds the groupings
	 * @return List of decoded Nodes
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
	 * @return List of decoded Nodes
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
	 * @return Map of Population UUID keys and index values
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
	 * @param measureConfig The measure configuration for the current measure.
	 */
	private void encodeMultiPerformanceChildren(JsonWrapper wrapper, List<Node> subPopNodes, final MeasureConfig measureConfig) {
		JsonWrapper childWrapper = new JsonWrapper();
		childWrapper.putBoolean(IS_END_TO_END_REPORTED, TRUE);
		JsonWrapper strataListWrapper = new JsonWrapper();
		for (Node subPopNode : subPopNodes) {
			JsonWrapper strataWrapper = new JsonWrapper();
			encodeSubPopulation(subPopNode, strataWrapper, true, measureConfig);
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
	 * @param measureConfig The measure configuration for the current measure.
	 */
	private void encodeSubPopulation(Node parentNode, JsonWrapper childWrapper, boolean isMultiRate,
		final MeasureConfig measureConfig) {
		this.encodePerformanceMet(childWrapper, parentNode);
		this.encodePerformanceNotMet(childWrapper, parentNode);

		for (Node childNode : parentNode.getChildNodes()) {
			JsonOutputEncoder measureDataEncoder = encoders.get(childNode.getType());
			if (null != measureDataEncoder) {
				measureDataEncoder.encode(childWrapper, childNode);
			}
		}
		if (isMultiRate) {
			this.encodeStratum(childWrapper, parentNode, measureConfig);
		}
	}

	/**
	 * Encodes a performance met from a numerator node
	 *
	 * @param wrapper holder of the encoded numerator node
	 * @param parentNode holder of the the numerator node
	 */
	private void encodePerformanceMet(JsonWrapper wrapper, Node parentNode) {
		Node numeratorNode = parentNode.findChildNode(n -> SubPopulations.NUMER.equals(n.getValue(TYPE)));
		Optional.ofNullable(numeratorNode).ifPresent(
			node -> {
				Node aggCount = node.getChildNodes().get(0);
				maintainContinuity(wrapper, aggCount, "performanceMet");
				wrapper.putInteger("performanceMet", aggCount.getValue(AGGREGATE_COUNT));
			});
	}


	/**
	 * Adds the Stratum attribute to the QPP document
	 *
	 * @param wrapper JsonWrapper
	 * @param parentNode Node
	 * @param measureConfig The measure configuration for the current measure.
	 */
	private void encodeStratum(JsonWrapper wrapper, Node parentNode, final MeasureConfig measureConfig) {
		Node numeratorNode = parentNode.findChildNode(n -> SubPopulations.NUMER.equals(n.getValue(TYPE)));
		Optional.ofNullable(numeratorNode).ifPresent(
				node -> {
					maintainContinuity(wrapper, node, "stratum");
					String numeratorPopulationId = node.getValue(MeasureDataDecoder.MEASURE_POPULATION);
					String stratum = stratumForNumeratorUuid(numeratorPopulationId, measureConfig);
					wrapper.putString("stratum", stratum);
				});
	}

	/**
	 * Given the numerator UUID, return the associated strata name that contains that numerator.
	 *
	 * @param numeratorUuid The numerator to search for.
	 * @param measureConfig The measure configuration to search within.
	 * @return The strata name.
	 */
	private String stratumForNumeratorUuid(String numeratorUuid, MeasureConfig measureConfig) {
		List<Strata> stratas = measureConfig.getStrata();
		Optional<String> possibleStratum = stratas.stream()
			.filter(strata -> numeratorUuid.equals(strata.getElectronicMeasureUuids().getNumeratorUuid()))
			.map(Strata::getName)
			.findFirst();

		return possibleStratum.orElse(numeratorUuid);
	}

	/**
	 * Encodes a performance not met from denominator and denominator exclusion
	 *
	 * @param wrapper holder of the encoded denominator and denominator exclusion nodes
	 * @param parentNode holder of the denominator and denominator exclusion nodes
	 */
	private void encodePerformanceNotMet(JsonWrapper wrapper, Node parentNode) {
		Node numeratorNode = parentNode.findChildNode(n -> SubPopulations.NUMER.equals(n.getValue(TYPE)));
		Node denominatorNode = parentNode.findChildNode(n -> SubPopulations.DENOM.equals(n.getValue(TYPE)));
		Node denomExclusionNode = parentNode.findChildNode(n -> SubPopulations.DENEX.equals(n.getValue(TYPE)));
		Node denomExceptionNode = parentNode.findChildNode(n -> SubPopulations.DENEXCEP.equals(n.getValue(TYPE)));

		Optional.ofNullable(denomExclusionNode).ifPresent(
				node -> {
					Node aggCount = node.getChildNodes().get(0);
					maintainContinuity(wrapper, aggCount, "eligiblePopulationExclusion");
					String value = aggCount.getValue(AGGREGATE_COUNT);
					wrapper.putInteger("eligiblePopulationExclusion", value);
				});

		Optional.ofNullable(denomExceptionNode).ifPresent(
				node -> {
					Node aggCount = node.getChildNodes().get(0);
					maintainContinuity(wrapper, aggCount, "eligiblePopulationException");
					String value = aggCount.getValue(AGGREGATE_COUNT);
					wrapper.putInteger("eligiblePopulationException", value);
				});

		Optional.ofNullable(denominatorNode).ifPresent(
				node -> {
					String performanceNotMet = calculatePerformanceNotMet(numeratorNode, denominatorNode,
							denomExclusionNode, denomExceptionNode);
					Node aggCount = node.getChildNodes().get(0);
					//for eCQMs, will be equal to
					// denominator - numerator - denominator exclusion - denominator exception
					maintainContinuity(wrapper, aggCount, "performanceNotMet");
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
	private String calculatePerformanceNotMet(Node numeratorNode, Node denominatorNode,
											Node denomExclusionNode, Node denomExceptionNode) {

		String denominatorValue = denominatorNode == null ? "0" :
				denominatorNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);
		String denomExclusionValue = denomExclusionNode == null ? "0" :
				denomExclusionNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);
		String numeratorValue = numeratorNode == null ? "0" :
				numeratorNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);
		String denomExceptionValue = denomExceptionNode == null ? "0" :
				denomExceptionNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);

		// for eCQMs, will be equal to denominator - numerator - denominator exclusion - denominator exception
		return Integer.toString(Integer.parseInt(denominatorValue)
				- Integer.parseInt(numeratorValue)
				- Integer.parseInt(denomExclusionValue)
				- Integer.parseInt(denomExceptionValue));
	}
}
