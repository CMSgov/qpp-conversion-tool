package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.Strata;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.util.MeasureConfigHelper;
import gov.cms.qpp.conversion.util.SubPopulationHelper;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;

/**
 * Encoder to serialize Quality Measure Identifier and Measure Sections
 */
@Encoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdEncoder extends QppOutputEncoder {

	private static final String MEASURE_ID = "measureId";
	private static final String TYPE = "type";
	private static final String SINGLE_PERFORMANCE_RATE = "singlePerformanceRate";
	public static final String IS_END_TO_END_REPORTED = "isEndToEndReported";
	private static final String TRUE = "true";
	private static final String MEASURE_438 = "438";
	private static final String PERFORMANCE_NOT_MET = "performanceNotMet";

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
		MeasureConfig measureConfig = MeasureConfigHelper.getMeasureConfig(node);
		String measureId = measureConfig.getMeasureId();
		wrapper.putString(MEASURE_ID, measureId);
		if (MEASURE_438.equals(measureId)) {
			encodeAllSubPopulationSums(wrapper, node);
		} else if (isASinglePerformanceRate(measureConfig) && !MEASURE_438.equals(measureId)) {
			encodeChildren(wrapper, node, measureConfig);
		} else {
			encodeMultiPerformanceRate(wrapper, node, measureConfig);
		}
	}

	/**
	 * Encodes the sum of the all Sub-populations into one set of Sub-populations
	 *
	 * @param wrapper JsonWrapper that will represent the Quality Measure Identifier
	 * @param separateSubPopulationNode Parent node holding all SubPopulations
	 *
	 */
	private void encodeAllSubPopulationSums(JsonWrapper wrapper, Node separateSubPopulationNode) {
		JsonWrapper childWrapper = new JsonWrapper();
		childWrapper.putBoolean(IS_END_TO_END_REPORTED, TRUE);

		encodeSubPopulationSum(SubPopulationLabel.NUMER, separateSubPopulationNode, childWrapper);
		encodePerformanceNotMetSubPopulationSum(childWrapper, separateSubPopulationNode);
		encodeSubPopulationSum(SubPopulationLabel.DENOM, separateSubPopulationNode, childWrapper);
		encodeSubPopulationSum(SubPopulationLabel.DENEX, separateSubPopulationNode, childWrapper);
		encodeSubPopulationSum(SubPopulationLabel.DENEXCEP, separateSubPopulationNode, childWrapper);

		wrapper.putObject(VALUE, childWrapper);
	}

	/**
	 *
	 *
	 * @param label current Sub-Population type
	 * @param measureReferenceNode holder of measure data nodes
	 * @param childWrapper wrapper to hold encoded measure data
	 */
	private void encodeSubPopulationSum(SubPopulationLabel label, Node measureReferenceNode, JsonWrapper childWrapper) {
		int currentPopulationSum = calculateSubPopulationSum(measureReferenceNode, label);
		maintainContinuity(childWrapper, measureReferenceNode, SubPopulationHelper.measureTypeMap.get(label));
		childWrapper.putInteger(SubPopulationHelper.measureTypeMap.get(label), String.valueOf(currentPopulationSum));
	}

	/**
	 *
	 *
	 * @param childWrapper wrapper to hold encoded measure data
	 * @param measureReferenceNode holder of measure data nodes
	 */
	private void encodePerformanceNotMetSubPopulationSum(JsonWrapper childWrapper, Node measureReferenceNode) {
		int numeratorSum = calculateSubPopulationSum(measureReferenceNode, SubPopulationLabel.NUMER);
		int denominatorSum = calculateSubPopulationSum(measureReferenceNode, SubPopulationLabel.DENOM);
		int denexSum = calculateSubPopulationSum(measureReferenceNode, SubPopulationLabel.DENEX);
		int denexcepSum = calculateSubPopulationSum(measureReferenceNode, SubPopulationLabel.DENEXCEP);
		int performanceNotMet = denominatorSum - numeratorSum - denexSum - denexcepSum;

		maintainContinuity(childWrapper, measureReferenceNode, PERFORMANCE_NOT_MET);
		childWrapper.putInteger(PERFORMANCE_NOT_MET, String.valueOf(performanceNotMet));
	}

	/**
	 *
	 *
	 * @param measureReferenceNode holder of measure data nodes
	 * @param label current Sub-Population type
	 * @return
	 */
	private int calculateSubPopulationSum(Node measureReferenceNode, SubPopulationLabel label) {
		return measureReferenceNode.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2)
			.filter(childNode ->
				label.hasAlias(childNode.getValue(MeasureDataDecoder.MEASURE_TYPE)))
			.mapToInt(ipopNode ->
				Integer.parseInt(ipopNode.findFirstNode(TemplateId.PI_AGGREGATE_COUNT).getValue(AGGREGATE_COUNT))).sum();
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
					Integer subPopIndex = mapPopulationIdToSubPopIndex.get(populationId.toUpperCase(Locale.ENGLISH));
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
			if (TemplateId.MEASURE_DATA_CMS_V2 == childNode.getType()) {
				JsonOutputEncoder measureDataEncoder = encoders.get(childNode.getType());
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
		Node numeratorNode = parentNode.findChildNode(n -> SubPopulationLabel.NUMER.hasAlias(n.getValue(TYPE)));
		Optional.ofNullable(numeratorNode).ifPresent(
			node -> {
				Node aggCount = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);
				maintainContinuity(wrapper, aggCount, SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.NUMER));
				wrapper.putInteger(SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.NUMER),
					aggCount.getValue(AggregateCountDecoder.AGGREGATE_COUNT));
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
		Node numeratorNode = parentNode.findChildNode(n -> SubPopulationLabel.NUMER.hasAlias(n.getValue(TYPE)));
		Optional.ofNullable(numeratorNode).ifPresent(
				node -> {
					maintainContinuity(wrapper, node, "stratum");
					String numeratorPopulationId =
							node.getValue(MeasureDataDecoder.MEASURE_POPULATION).toUpperCase(Locale.ENGLISH);
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
		Node numeratorNode = parentNode.findChildNode(n -> SubPopulationLabel.NUMER.hasAlias(n.getValue(TYPE)));
		Node denominatorNode = parentNode.findChildNode(n -> SubPopulationLabel.DENOM.hasAlias(n.getValue(TYPE)));
		Node denomExclusionNode = parentNode.findChildNode(n -> SubPopulationLabel.DENEX.hasAlias(n.getValue(TYPE)));
		Node denomExceptionNode = parentNode.findChildNode(n -> SubPopulationLabel.DENEXCEP.hasAlias(n.getValue(TYPE)));

		Optional.ofNullable(denomExclusionNode).ifPresent(
				node -> {
					Node aggCount = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);
					maintainContinuity(wrapper, aggCount, SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.DENEX));
					String value = aggCount.getValue(AggregateCountDecoder.AGGREGATE_COUNT);
					wrapper.putInteger(SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.DENEX), value);
				});

		Optional.ofNullable(denomExceptionNode).ifPresent(
				node -> {
					Node aggCount = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);
					maintainContinuity(wrapper, aggCount, SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.DENEXCEP));
					String value = aggCount.getValue(AggregateCountDecoder.AGGREGATE_COUNT);
					wrapper.putInteger(SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.DENEXCEP), value);
				});

		Optional.ofNullable(denominatorNode).ifPresent(
				node -> {
					String performanceNotMet = calculatePerformanceNotMet(numeratorNode, denominatorNode,
							denomExclusionNode, denomExceptionNode);
					Node aggCount = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);
					//for eCQMs, will be equal to
					// denominator - numerator - denominator exclusion - denominator exception
					maintainContinuity(wrapper, aggCount, PERFORMANCE_NOT_MET);
					wrapper.putInteger(PERFORMANCE_NOT_MET, performanceNotMet);
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
				denominatorNode.findFirstNode(TemplateId.PI_AGGREGATE_COUNT).getValue(AggregateCountDecoder.AGGREGATE_COUNT);
		String denomExclusionValue = denomExclusionNode == null ? "0" :
				denomExclusionNode.findFirstNode(TemplateId.PI_AGGREGATE_COUNT).getValue(AggregateCountDecoder.AGGREGATE_COUNT);
		String numeratorValue = numeratorNode == null ? "0" :
				numeratorNode.findFirstNode(TemplateId.PI_AGGREGATE_COUNT).getValue(AggregateCountDecoder.AGGREGATE_COUNT);
		String denomExceptionValue = denomExceptionNode == null ? "0" :
				denomExceptionNode.findFirstNode(TemplateId.PI_AGGREGATE_COUNT).getValue(AggregateCountDecoder.AGGREGATE_COUNT);

		// for eCQMs, will be equal to denominator - numerator - denominator exclusion - denominator exception
		return Integer.toString(Integer.parseInt(denominatorValue)
				- Integer.parseInt(numeratorValue)
				- Integer.parseInt(denomExclusionValue)
				- Integer.parseInt(denomExceptionValue));
	}
}
