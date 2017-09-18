package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

/**
 * Validates a Measure Reference Results node.
 */
@Validator(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdValidator extends NodeValidator {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QualityMeasureIdValidator.class);
	protected static final String MEASURE_ID = "measureId";

	static final String MEASURE_GUID_MISSING = "The measure reference results must have a measure GUID";
	public static final String SINGLE_MEASURE_POPULATION =
			"The measure reference results must have a single measure population";
	public static final String SINGLE_MEASURE_TYPE =
			"The measure reference results must have a single measure type";
	static final String NO_CHILD_MEASURE = "The measure reference results must have at least one measure";
	public static final String REQUIRED_CHILD_MEASURE =
			"The eCQM (electronic measure id: %s) requires one and only one %s";
	protected static final String INCORRECT_UUID = "The eCQM (electronic measure id: %s) requires a %s with the correct UUID";
	protected static final String NO_CHILD_POPULATION_CRITERIA_NEEDED =
		"The eCQM (electronic measure id: %s) does not need a %s but one was supplied";
	protected static final String DENEX = "denominator exclusion";
	protected static final String DENEXCEP = "eligiblePopulationExclusion";
	protected static final String NUMER = "performanceMet";
	public static final String DENOM = "eligiblePopulation";

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

		for (SubPopulation subPopulation : subPopulations) {
			validateSubPopulation(node, subPopulation);
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
			Arrays.asList(makeValidator(subPopulation::getDenominatorExceptionsUuid, DENEXCEP, "DENEXCEP"),
				makeValidator(subPopulation::getDenominatorExclusionsUuid, DENEX, "DENEX"),
				makeValidator(subPopulation::getNumeratorUuid, NUMER, "NUMER"),
				makeValidator(subPopulation::getDenominatorUuid, DENOM, "DENOM"));

		validations.forEach(validate -> validate.accept(node));
	}

	/**
	 * Method template for measure validations.
	 *
	 * @param check a property existence check
	 * @param key that identify measures
	 * @param label a short measure description
	 * @return a callback / consumer that will perform a measure specific validation against a given
	 * node.
	 */
	private Consumer<Node> makeValidator(Supplier<Object> check, String label, String key) {
		return node -> {
			Predicate<Node> childTypeFinder = makeTypeChildFinder(key);

			if (check.get() != null) {
				//we want a NUMER and a specific UUID for NUMER
				//get the number of NUMERs, regardless of UUID
				long childTypeCount = node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2).filter(childTypeFinder).count();
				if (childTypeCount == 1) {
					//we have exactly 1 NUMER, good
					//now, we need to check the UUID to see if it is correct
					Predicate<Node> childUuidFinder = makeUuidChildFinder(check);
					long childUuidCount = node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2).filter(childTypeFinder).filter(childUuidFinder).count();

					if (childUuidCount != 1) {
						//we don't have the appropriate NUMER/UUID combo
						//bad
						MeasureConfig config =
							MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
						String message = String.format(INCORRECT_UUID, config.getElectronicMeasureId(), label);
						this.getDetails().add(new Detail(message, node.getPath()));
					}

				} else {
					//we either have 0 NUMERs (or whatever) or we have too many NUMERs
					//bad, validation error
					MeasureConfig config =
						MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
					String message = String.format(REQUIRED_CHILD_MEASURE, config.getElectronicMeasureId(), label);
					this.getDetails().add(new Detail(message, node.getPath()));
				}
			} else {
				//we don't want a NUMER
				boolean containsChildMeasureNode = node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2).anyMatch(childTypeFinder);
				if (containsChildMeasureNode) {
					MeasureConfig config = MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
					String message = String.format(NO_CHILD_POPULATION_CRITERIA_NEEDED, config.getElectronicMeasureId(), label);
					this.getDetails().add(new Detail(message, node.getPath()));
				}
			}
		};
	}

	private Predicate<Node> makeTypeChildFinder(String populationCriteriaType) {
		return thisNode -> {
			thoroughlyCheck(thisNode)
				.incompleteValidation()
				.singleValue(SINGLE_MEASURE_TYPE, MEASURE_TYPE);
			return populationCriteriaType.equals(thisNode.getValue(MEASURE_TYPE));
		};
	}

	private Predicate<Node> makeUuidChildFinder(Supplier<Object> uuid) {
		return thisNode -> {
			thoroughlyCheck(thisNode)
				.incompleteValidation()
				.singleValue(SINGLE_MEASURE_POPULATION, MEASURE_POPULATION);
			return uuid.get().equals(thisNode.getValue(MEASURE_POPULATION));
		};
	}

	/**
	 * Search filter for child measure nodes.
	 *
	 * @param check provides sub population specific measure id
	 * @param key that identifies measure
	 * @return search filter
	 */
	private Predicate<Node> makeChildFinder(Supplier<Object> check, String key) {
		return thisNode -> {
			thoroughlyCheck(thisNode)
					.incompleteValidation()
					.singleValue(SINGLE_MEASURE_TYPE, MEASURE_TYPE)
					.singleValue(SINGLE_MEASURE_POPULATION, MEASURE_POPULATION);
			boolean validMeasureType = key.equals(thisNode.getValue(MEASURE_TYPE));
			return validMeasureType && check.get().equals(thisNode.getValue(MEASURE_POPULATION));
		};
	}
}
