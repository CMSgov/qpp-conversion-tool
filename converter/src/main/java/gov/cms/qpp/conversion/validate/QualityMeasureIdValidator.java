package gov.cms.qpp.conversion.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import gov.cms.qpp.conversion.util.MeasureConfigHelper;
import gov.cms.qpp.conversion.util.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.cms.qpp.conversion.model.Constants.*;

/**
 * Validates a Measure Reference Results node.
 */
abstract class QualityMeasureIdValidator extends NodeValidator {
	Set<SubPopulationLabel> subPopulationExclusions = Collections.emptySet();
	protected static final String NOT_AVAILABLE = "(not provided)";
	protected static final Set<String> IPOP = Stream.of("IPP", "IPOP")
			.collect(Collectors.toSet());
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QualityMeasureIdValidator.class);

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
	protected void performValidation(final Node node) {
		//It is possible that we have a Measure in the input that we have not defined in
		//the meta data measures-data.json
		//This should not be an error
		forceCheckErrors(node)
				.singleValue(ProblemCode.MISSING_OR_DUPLICATED_MEASURE_GUID, MeasureConfigHelper.MEASURE_ID)
				.childMinimum(ProblemCode.CHILD_MEASURE_MISSING, 1, TemplateId.MEASURE_DATA_CMS_V4);
		validateMeasureConfigs(node);
	}

	/**
	 * Validate measure configurations
	 *
	 * @param node to validate
	 */
	private void validateMeasureConfigs(Node node) {
		MeasureConfig measureConfig = MeasureConfigHelper.getMeasureConfig(node);

		if (measureConfig != null) {
			validateAllSubPopulations(node, measureConfig);
		} else {
			String value = node.getValue(MeasureConfigHelper.MEASURE_ID);
			if (value != null) { // This check has already been made and a detail will exist if value is null.
				DEV_LOG.error(ProblemCode.MEASURE_GUID_MISSING.name() + " " + value);
				LocalizedProblem error = ProblemCode.MEASURE_GUID_MISSING.format(value, Context.REPORTING_YEAR);
				addError(Detail.forProblemAndNode(error, node));
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

		if (MeasureConfigHelper.SINGLE_TO_MULTI_PERF_RATE_MEASURE_ID.equalsIgnoreCase(measureConfig.getMeasureId())) {
			List<SubPopulation> replacementList = new ArrayList<>();
			for (SubPopulation subPopulation: subPopulations) {
				if (null != subPopulation) {
					replacementList.add(subPopulation);
				}
			}
			subPopulations = replacementList;
		}

		final List<SubPopulation> finalSubPopulationList = subPopulations;
		SubPopulations.getExclusiveKeys(subPopulationExclusions)
				.forEach(subPopulationLabel -> validateChildTypeCount(finalSubPopulationList, subPopulationLabel, node));

		for (SubPopulation subPopulation : finalSubPopulationList) {
			validateSubPopulation(node, subPopulation, measureConfig.getMeasureId());
		}
	}

	/**
	 * Validates that given subpopulations have the correct number of a given type
	 *
	 * @param subPopulations The subpopulations to test against
	 * @param key The type to check
	 * @param node The node in which the child nodes live
	 */
	private void validateChildTypeCount(List<SubPopulation> subPopulations, SubPopulationLabel key, Node node) {
		long expectedChildTypeCount = subPopulations.stream()
			.map(subPopulation -> SubPopulations.getUniqueIdForKey(key.name(), subPopulation))
			.filter(Objects::nonNull)
			.count();

		Predicate<Node> childTypeFinder = makeTypeChildFinder(key.getAliases());
		long actualChildTypeCount = node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V4).filter(childTypeFinder).count();

		if (expectedChildTypeCount != actualChildTypeCount) {
			LocalizedProblem error =
				ProblemCode.POPULATION_CRITERIA_COUNT_INCORRECT.format(
					MeasureConfigHelper.getMeasureConfig(node).getElectronicMeasureId(),
					expectedChildTypeCount, StringHelper.join(key.getAliases(), ",", "or"),
					actualChildTypeCount);
			Detail detail = Detail.forProblemAndNode(error, node);
			addError(detail);
		}
	}

	/**
	 * Validate individual sub-populations.
	 *
	 * @param node          to validate
	 * @param subPopulation a grouping of measures
	 */
	private void validateSubPopulation(Node node, SubPopulation subPopulation, String measureId) {
		List<Consumer<Node>> validations = prepValidations(subPopulation);
		validations.forEach(validate -> validate.accept(node));

		validateDenomCountToIpopCount(node, subPopulation, measureId);
	}


	/**
	 * Each concrete implementations have the flexibility to  define the preparations
	 * they need for the specific validations they perform.
	 * 
	 * Called by validateSubPopulations
	 *  
	 * @param subPopulation
	 * @return
	 */
	abstract List<Consumer<Node>> prepValidations(SubPopulation subPopulation);

	/**
	 * Validation check for Denominator and Numerator counts of the same Sub Population
	 *
	 * @param node The current parent node
	 * @param subPopulation the current sub population
	 */
	protected void validateDenomCountToIpopCount(Node node, SubPopulation subPopulation, String measureId) {
		Node denomNode = getDenominatorNodeFromCurrentSubPopulation(node, subPopulation);
		Node ipopNode = getIpopNodeFromCurrentSubPopulation(node, subPopulation);
		String program = node.getParent().getParent().getValue(PROGRAM_NAME);

		if (denomNode != null && ipopNode != null) {
			Node denomCount = denomNode.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);
			Node ipopCount = ipopNode.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);

			if ((CPCPLUS_PROGRAM_NAME.equalsIgnoreCase(program)
				&& MeasureConfigHelper.CPC_PLUS_MEASURES.contains(measureId))
					|| PCF_PROGRAM_NAME.equalsIgnoreCase(program)) {
				validateCpcDenominatorCount(denomCount, ipopCount, subPopulation.getDenominatorUuid(), program);
			} else {
				validateDenominatorCount(denomCount, ipopCount, subPopulation.getDenominatorUuid());
			}
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
		return node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V4).filter(thisNode ->
				SubPopulationLabel.DENOM.hasAlias(thisNode.getValue(MEASURE_TYPE))
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
		return node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V4).filter(thisNode ->
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
	private void validateDenominatorCount(Node denomCount, Node ipopCount, String denominatorUuid) {
		forceCheckErrors(denomCount)
				.incompleteValidation()
				.intValue(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER,
						AGGREGATE_COUNT)
				.lessThanOrEqualTo(ProblemCode.DENOMINATOR_COUNT_INVALID.format(denominatorUuid, Context.REPORTING_YEAR),
					Integer.parseInt(ipopCount.getValue(AGGREGATE_COUNT)));
	}

	/**
	 * Performs a validation on a CPC Denominator node's aggregate count to the Initial Population node's aggregate count
	 *
	 * @param denomCount Aggregate Count node of denominator
	 * @param ipopCount Aggregate Count node of initial population
	 */
	private void validateCpcDenominatorCount(Node denomCount, Node ipopCount, String denominatorUuid, String program) {
		forceCheckErrors(denomCount)
			.incompleteValidation()
			.intValue(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER,
				AGGREGATE_COUNT)
			.valueIn(ProblemCode.PCF_DENOMINATOR_COUNT_INVALID.format(program, denominatorUuid, Context.REPORTING_YEAR), AGGREGATE_COUNT,
				ipopCount.getValue(AGGREGATE_COUNT));
	}

	/**
	 * Method template for measure validations.
	 *
	 * @param sub {@link SubPopulation} against which follow validations may be performed
	 * @param check a property existence check
	 * @param subPopulationLabel that houses sub-population aliases
	 * @return a callback / consumer that will perform a measure specific validation against a given
	 * node.
	 */
	Consumer<Node> makeValidator(SubPopulation sub, Supplier<String> check, SubPopulationLabel subPopulationLabel) {
		return node -> {
			if (check.get() != null) {
				String[] keys = subPopulationLabel.getAliases();
				Predicate<Node> childTypeFinder = makeTypeChildFinder(keys);
				Predicate<Node> childUuidFinder =
						makeUuidChildFinder(check, ProblemCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION,
								MEASURE_POPULATION);

				Node existingUuidChild = node
						.getChildNodes(TemplateId.MEASURE_DATA_CMS_V4)
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
	 * @param node a {@link TemplateId#MEASURE_DATA_CMS_V4} node
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
	protected void addMeasureConfigurationValidationMessage(Supplier<String> check, String[] keys, Node node) {
		LocalizedProblem error = ProblemCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(
				MeasureConfigHelper.getMeasureConfig(node).getElectronicMeasureId(),
				String.join(",", keys), check.get());
		addError(Detail.forProblemAndNode(error, node));
	}

	/**
	 * Creates a {@link Predicate} which takes a node and tests whether the measure type is equal to the given measure type.
	 * Also validates that it's the only measure type in the given node.
	 *
	 * @param populationCriteriaTypes measure type i.e. "DENOM", "NUMER", ...
	 * @return predicate that filters measure nodes by measure type
	 */
	protected Predicate<Node> makeTypeChildFinder(String... populationCriteriaTypes) {
		return thisNode -> {
			forceCheckErrors(thisNode)
					.incompleteValidation()
					.singleValue(ProblemCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE, MEASURE_TYPE);
			return Arrays.asList(populationCriteriaTypes).contains(thisNode.getValue(MEASURE_TYPE));
		};
	}

	/**
	 * Creates a {@link Predicate} which takes a node and tests whether the measure population is equal to the given unique id
	 *
	 * @param uuid Supplies a unique id to test against
	 * @param error Supplies a unique error message and error code to use
	 * @param name Supplies a node field validate on
	 * @return predicate seeking a matching uuid
	 */
	protected Predicate<Node> makeUuidChildFinder(Supplier<String> uuid, LocalizedProblem error, String name) {
		return thisNode -> {
			forceCheckErrors(thisNode)
					.incompleteValidation()
					.singleValue(error, name);
			return uuid.get().equalsIgnoreCase(thisNode.getValue(name));
		};
	}

}
