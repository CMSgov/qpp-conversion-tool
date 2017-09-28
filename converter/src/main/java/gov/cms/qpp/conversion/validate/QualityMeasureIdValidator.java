package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.decode.StratifierDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

/**
 * Validates a Measure Reference Results node.
 */
@Validator(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdValidator extends NodeValidator {
	protected static final Set<String> IPOP = Stream.of("IPP", "IPOP")
			.collect(Collectors.toSet());
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QualityMeasureIdValidator.class);

	public static final String MEASURE_ID = "measureId";
	static final String MEASURE_GUID_MISSING = "The measure reference results must have a measure GUID";
	public static final String SINGLE_MEASURE_POPULATION =
			"The measure reference results must have a single measure population";
	public static final String SINGLE_MEASURE_TYPE =
			"The measure reference results must have a single measure type";
	static final String NO_CHILD_MEASURE = "The measure reference results must have at least one measure";
	public static final String REQUIRE_VALID_DENOMINATOR_COUNT =
			"The Denominator count must be less than or equal to Initial Population count "
					+ "for an eCQM that is proportion measure";
	public static final String INCORRECT_POPULATION_CRITERIA_COUNT =
			"The eCQM (electronic measure id: %s) requires %d %s(s) but there are %d";
	private static final String MISSING_STRATA = "Missing strata %s for %s measure (%s)";
	private static final String STRATA_MISMATCH = "Amount of stratifications %d does not meet expectations %d" +
			"for %s measure (%s). Expected strata: %s";

	static final String INCORRECT_UUID =
			"The eCQM (electronic measure id: %s) requires a %s with the correct UUID of %s";

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
			.singleValue(MEASURE_GUID_MISSING, MEASURE_ID)
			.childMinimum(NO_CHILD_MEASURE, 1, TemplateId.MEASURE_DATA_CMS_V2);
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
				this.addValidationError(new Detail(MEASURE_GUID_MISSING, node.getPath()));
			}
		}
	}

	/**
	 * Validates all the sub populations in the quality measure based on the measure configuration
	 *
	 * @param node The current parent node
	 * @param measureConfig The measure configuration's sub population to use
	 */
	private void validateAllSubPopulations(final Node node, final MeasureConfig measureConfig) {
		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();

		if (subPopulations.isEmpty()) {
			return;
		}

		SubPopulations.getKeys().forEach(key -> validateChildTypeCount(subPopulations, key, node));

		for (SubPopulation subPopulation : subPopulations) {
			validateSubPopulation(node, subPopulation);
		}
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
			MeasureConfig config =
					MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
			String message = String.format(INCORRECT_POPULATION_CRITERIA_COUNT,
					config.getElectronicMeasureId(), expectedChildTypeCount, key, actualChildTypeCount);
			this.getDetails().add(new Detail(message, node.getPath()));
		}
	}

	/**
	 * Validate individual sub-populations.
	 *
	 * @param node to validate
	 * @param subPopulation a grouping of measures
	 */
	private void validateSubPopulation(Node node, SubPopulation subPopulation) {

		List<Consumer<Node>> validations =
			Arrays.asList(makeValidator(subPopulation, subPopulation::getDenominatorExceptionsUuid, "DENEXCEP"),
				makeValidator(subPopulation, subPopulation::getDenominatorExclusionsUuid, "DENEX"),
				makeValidator(subPopulation, subPopulation::getNumeratorUuid, "NUMER"),
				makeValidator(subPopulation, subPopulation::getDenominatorUuid, "DENOM"));

		validations.forEach(validate -> validate.accept(node));

		validateDenomCountToIpopCount(node, subPopulation);
	}

	/**
	 * Method template for measure validations.
	 *
	 * @param check a property existence check
	 * @param key that identify measures
	 * @return a callback / consumer that will perform a measure specific validation against a given
	 * node.
	 */
	private Consumer<Node> makeValidator(SubPopulation sub, Supplier<Object> check, String key) {
		return node -> {
			if (check.get() != null) {
				Predicate<Node> childTypeFinder = makeTypeChildFinder(key);
				Predicate<Node> childUuidFinder = makeUuidChildFinder(check, sub);

				Node existingUuidChild = node
						.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2)
						.filter(childTypeFinder)
						.filter(childUuidFinder)
						.findFirst()
						.orElse(null);

				if (existingUuidChild == null) {
					MeasureConfig config =
						MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
					String message = String.format(INCORRECT_UUID, config.getElectronicMeasureId(), key, check.get());
					this.getDetails().add(new Detail(message, node.getPath()));
				} else {
					strataCheck(existingUuidChild, sub);
				}
			}
		};
	}

	/**
	 * Creates a {@link Predicate} which takes a node and tests whether the measure type is equal to the given measure type.
	 * Also validates that it's the only measure type in the given node.
	 *
	 * @param populationCriteriaType measure type i.e. "DENOM", "NUMER", ...
	 * @return predicate that filters measure nodes by measure type
	 */
	private Predicate<Node> makeTypeChildFinder(String populationCriteriaType) {
		return thisNode -> {
			thoroughlyCheck(thisNode)
				.incompleteValidation()
				.singleValue(SINGLE_MEASURE_TYPE, MEASURE_TYPE);
			return populationCriteriaType.equals(thisNode.getValue(MEASURE_TYPE));
		};
	}

	/**
	 * Creates a {@link Predicate} which takes a node and tests whether the measure population is equal to the given unique id
	 *
	 * @param uuid Supplies a unique id to test against
	 * @return predicate identifying a node with matching id
	 */
	private Predicate<Node> makeUuidChildFinder(Supplier<Object> uuid, SubPopulation sub) {
		return thisNode -> {
			thoroughlyCheck(thisNode)
				.incompleteValidation()
				.singleValue(SINGLE_MEASURE_POPULATION, MEASURE_POPULATION);
			return uuid.get().equals(thisNode.getValue(MEASURE_POPULATION));
		};
	}

	/**
	 * Validate measure strata
	 *
	 * @param node measure node
	 * @param sub sub population constituent ids
	 */
	private void strataCheck(Node node, SubPopulation sub) {

		List<Node> strataNodes = node.getChildNodes(TemplateId.REPORTING_STRATUM_CMS)
				.collect(Collectors.toList());

		if (strataNodes.size() != sub.getStrata().size()) {
			String message = String.format(STRATA_MISMATCH, strataNodes.size(), sub.getStrata().size(),
					node.getValue(MeasureDataDecoder.MEASURE_TYPE),
					node.getValue(MeasureDataDecoder.MEASURE_POPULATION),
					sub.getStrata());
			this.getDetails().add(new Detail(message, node.getPath()));
		}

		sub.getStrata().forEach(strata -> {
			Predicate<Node> seek = child ->
					child.getValue(StratifierDecoder.STRATIFIER_ID).equals(strata);

			if (strataNodes.stream().noneMatch(seek)) {
				String message = String.format(MISSING_STRATA, strata,
						node.getValue(MeasureDataDecoder.MEASURE_TYPE),
						node.getValue(MeasureDataDecoder.MEASURE_POPULATION));
				this.getDetails().add(new Detail(message, node.getPath()));
			}
		});
	}

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
				"DENOM".equals(thisNode.getValue(MEASURE_TYPE))
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
				.intValue(AggregateCountValidator.TYPE_ERROR,
						AggregateCountDecoder.AGGREGATE_COUNT)
				.lessThanOrEqualTo(REQUIRE_VALID_DENOMINATOR_COUNT,
						Integer.parseInt(ipopCount.getValue(AggregateCountDecoder.AGGREGATE_COUNT)));
	}
}
