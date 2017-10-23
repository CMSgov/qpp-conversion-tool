package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

/**
 * Validates a Measure Reference Results node.
 */
abstract class QualityMeasureIdValidator extends NodeValidator {
	Set<String> subPopulationExclusions = Collections.emptySet();
	protected static final Set<String> IPOP = Stream.of("IPP", "IPOP")
			.collect(Collectors.toSet());
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QualityMeasureIdValidator.class);

	public static final String MEASURE_ID = "measureId";
	public static final String SINGLE_MEASURE_POPULATION =
			"The measure reference results must have a single measure population";
	public static final String SINGLE_MEASURE_TYPE =
			"The measure reference results must have a single measure type";
	public static final String INCORRECT_UUID =
			"The eCQM (electronic measure id: %s) requires a %s with the correct UUID of %s";
	public static final String INCORRECT_PERFORMANCE_UUID =
			"The eCQM (electronic measure id: %s) has a %s with an incorrect UUID of %s";
	public static final String SINGLE_PERFORMANCE_RATE =
			"A Performance Rate must contain a single Performance Rate UUID";

	/**
	 * Validates that the Measure Reference Results node contains...
	 *
	 * <ul>
	 *	 <li>A measure GUID.</li>
	 *	 <li>At least one quality measure.</li>
	 *	 <li>And validates the sub-populations</li>
	 * </ul>
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(final Node node) {
		//It is possible that we have a Measure in the input that we have not defined in
		//the meta data measures-data.json
		//This should not be an error

		thoroughlyCheck(node)
				.singleValue(ErrorCode.MEASURE_GUID_MISSING, MEASURE_ID)
				.childMinimum(ErrorCode.CHILD_MEASURE_MISSING, 1, TemplateId.MEASURE_DATA_CMS_V2);
		validateMeasureConfigs(node);
	}

	/**
	 * Validate measure configurations
	 *
	 * @param node to validate
	 */
	private void validateMeasureConfigs(Node node) {
		Map<String, MeasureConfig> configurationMap = MeasureConfigs.getConfigurationMap();
		String value = node.getValue(MEASURE_ID);
		MeasureConfig measureConfig = configurationMap.get(value);

		if (measureConfig != null) {
			validateAllSubPopulations(node, measureConfig);
		} else {
			if (value != null) { // This check has already been made and a detail will exist if value is null.
				DEV_LOG.error("MEASURE_GUID_MISSING " + value);
				
				addValidationError(Detail.forErrorCodeAndNode(ErrorCode.MEASURE_GUID_MISSING, node));
			}
		}
	}

	/**
	 * Validates all the sub populations in the quality measure based on the measure configuration
	 *
	 * @param node The current parent node
	 * @param measureConfig The measure configuration's sub population to use
	 */
	void validateAllSubPopulations(final Node node, final MeasureConfig measureConfig) {
		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();

		if (subPopulations.isEmpty()) {
			return;
		}

		SubPopulations.getExclusiveKeys(subPopulationExclusions)
				.forEach(key -> validateChildTypeCount(subPopulations, key, node));

		for (SubPopulation subPopulation : subPopulations) {
			validateSubPopulation(node, subPopulation);
		}
	}

	/**
	 * Validate individual sub-populations.
	 *
	 * @param node          to validate
	 * @param subPopulation a grouping of measures
	 */
	private void validateSubPopulation(Node node, SubPopulation subPopulation) {
		List<Consumer<Node>> validations = prepValidations(subPopulation);
		validations.forEach(validate -> validate.accept(node));

		validateDenomCountToIpopCount(node, subPopulation);
	}


	abstract List<Consumer<Node>> prepValidations(SubPopulation subPopulation);

	/**
	 * Validation check for Denominator and Numerator counts of the same Sub Population
	 *
	 * @param node The current parent node
	 * @param subPopulation the current sub population
	 */
	private void validateDenomCountToIpopCount(Node node, SubPopulation subPopulation) {
		Node denomNode = getDenominatorNodeFromCurrentSubPopulation(node, subPopulation);

		Node ipopNode = getIpopNodeFromCurrentSubPopulation(node, subPopulation);

		if (denomNode != null && ipopNode != null) {
			Node denomCount = denomNode.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);
			Node ipopCount = ipopNode.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);

			validateDenominatorCount(denomCount, ipopCount);
		}
	}

	/**
	 * Retrieves the Denominator from the current Sub Population given
	 *
	 * @param node The current parent node
	 * @param subPopulation The current sub population holding the denominator UUID
	 * @return the denominator node filtered by sub population or null if not found
	 */
	private Node getDenominatorNodeFromCurrentSubPopulation(Node node, SubPopulation subPopulation) {
		return node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2).filter(thisNode ->
				SubPopulations.DENOM.equals(thisNode.getValue(MEASURE_TYPE))
						&& subPopulation.getDenominatorUuid().equals(thisNode.getValue(MEASURE_POPULATION)))
				.findFirst().orElse(null);
	}

	/**
	 * Retrieves the Initial Population from the current Sub Population given
	 *
	 * @param node The current parent node
	 * @param subPopulation The current sub population holding the initial population UUID
	 * @return the initial population node filtered by sub population or null if not found
	 */
	private Node getIpopNodeFromCurrentSubPopulation(Node node, SubPopulation subPopulation) {
		return node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2).filter(thisNode ->
				(IPOP.contains(thisNode.getValue(MEASURE_TYPE)))
						&& subPopulation.getInitialPopulationUuid().equals(thisNode.getValue(MEASURE_POPULATION)))
				.findFirst().orElse(null);
	}

	/**
	 * Performs a validation on the Denominator node's aggregate count to the Initial Population node's aggregate count
	 *
	 * @param denomCount Aggregate Count node of denominator
	 * @param ipopCount Aggregate Count node of initial population
	 */
	private void validateDenominatorCount(Node denomCount, Node ipopCount) {
		thoroughlyCheck(denomCount)
				.incompleteValidation()
				.intValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER,
						AggregateCountDecoder.AGGREGATE_COUNT)
				.lessThanOrEqualTo(ErrorCode.DENOMINATOR_COUNT_INVALID,
						Integer.parseInt(ipopCount.getValue(AggregateCountDecoder.AGGREGATE_COUNT)));
	}

	/**
	 * Validates that given subpopulations have the correct number of a given type
	 *
	 * @param subPopulations The subpopulations to test against
	 * @param key The type to check
	 * @param node The node in which the child nodes live
	 */
	private void validateChildTypeCount(List<SubPopulation> subPopulations, String key, Node node) {
		long expectedChildTypeCount = subPopulations.stream()
				.map(subPopulation -> SubPopulations.getUniqueIdForKey(key, subPopulation))
				.filter(Objects::nonNull)
				.count();

		Predicate<Node> childTypeFinder = makeTypeChildFinder(key);
		long actualChildTypeCount = node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2).filter(childTypeFinder).count();

		if (expectedChildTypeCount != actualChildTypeCount) {
			MeasureConfig config = MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
			Detail detail = Detail.forErrorCodeAndNode(ErrorCode.POPULATION_CRITERIA_COUNT_INCORRECT, node);
			Detail.formatMessage(detail, config.getElectronicMeasureId(), expectedChildTypeCount, key, actualChildTypeCount);
			addValidationError(detail);
		}
	}

	/**
	 * Method template for measure validations.
	 *
	 * @param check a property existence check
	 * @param keys that identify measure
	 * @return a callback / consumer that will perform a measure specific validation against a given
	 * node.
	 */
	Consumer<Node> makeValidator(SubPopulation sub, Supplier<Object> check, String... keys) {
		return node -> {
			if (check.get() != null) {
				Predicate<Node> childTypeFinder = makeTypeChildFinder(keys);
				Predicate<Node> childUuidFinder =
						makeUuidChildFinder(check, SINGLE_MEASURE_POPULATION, MEASURE_POPULATION);

				Node existingUuidChild = node
						.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2)
						.filter(childTypeFinder)
						.filter(childUuidFinder)
						.findFirst()
						.orElse(null);

				if (existingUuidChild == null) {
					addMeasureConfigurationValidationMessage(check, keys, node);
				} else {
					followUpHook(existingUuidChild, sub);
				}
			}
		};
	}

	/**
	 * Validator hook to allow implementor the opportunity to perform sub population validations.
	 *
	 * @param node a {@link TemplateId#MEASURE_DATA_CMS_V2} node
	 * @param sub corresponding sub population
	 */
	protected void followUpHook(Node node, SubPopulation sub){
		//Default implementation
	}

	/**
	 * Adds a validation error message for a specified measure configuration
	 *
	 * @param check Current SubPopulation to be validated
	 * @param keys Identifiers for the current measures child
	 * @param node Contains the current child nodes
	 */
	protected void addMeasureConfigurationValidationMessage(Supplier<Object> check, String[] keys, Node node) {
		MeasureConfig config =
				MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
		String message = String.format(INCORRECT_UUID, config.getElectronicMeasureId(),
				String.join(",", Arrays.asList(keys)), check.get());
		addValidationError(new Detail(message, node.getPath()));
	}

	/**
	 * Creates a {@link Predicate} which takes a node and tests whether the measure type is equal to the given measure type.
	 * Also validates that it's the only measure type in the given node.
	 *
	 * @param populationCriteriaTypes measure type i.e. "DENOM", "NUMER", ...
	 * @return predicate that filters measure nodes by measure type
	 */
	private Predicate<Node> makeTypeChildFinder(String... populationCriteriaTypes) {
		return thisNode -> {
			thoroughlyCheck(thisNode)
					.incompleteValidation()
					.singleValue(SINGLE_MEASURE_TYPE, MEASURE_TYPE);
			return Arrays.asList(populationCriteriaTypes).contains(thisNode.getValue(MEASURE_TYPE));
		};
	}

	/**
	 * Creates a {@link Predicate} which takes a node and tests whether the measure population is equal to the given unique id
	 *
	 * @param uuid Supplies a unique id to test against
	 * @param message Supplies a unique error message to use
	 * @param name Supplies a node field validate on
	 * @return predicate seeking a matching uuid
	 */
	protected Predicate<Node> makeUuidChildFinder(Supplier<Object> uuid, String message, String name) {
		return thisNode -> {
			thoroughlyCheck(thisNode)
					.incompleteValidation()
					.singleValue(message, name);
			return uuid.get().equals(thisNode.getValue(name));
		};
	}
}
