package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;

/** Validates a Measure Reference Results node. */
@Validator(templateId = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdValidator extends NodeValidator {
	static final String MEASURE_GUID_MISSING =
			"The measure reference results must have a measure GUID";
	static final String NO_CHILD_MEASURE =
			"The measure reference results must have at least one measure";
	static final String REQUIRED_CHILD_MEASURE = "The eCQM measure requires a %s";

	/**
	 * Validates that the Measure Reference Results node contains...
	 *
	 * <ul>
	 *	 <li>A measure GUID.
	 *	 <li>At least one quality measure.
	 * </ul>
	 *
	 * @param node The node to validate.
	 */

	@Override
	protected void internalValidateSingleNode(final Node node) {
		thoroughlyCheck(node)
				.value(MEASURE_GUID_MISSING, MEASURE_TYPE)
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

		MeasureConfig measureConfig = configurationMap.get(node.getValue(MEASURE_TYPE));

		if (measureConfig != null) {
			validateAllSubPopulations(measureConfig, node);
		}
	}

	/**
	 * Validate sub-populations
	 *
	 * @param measureConfig Measure configuration meta data
	 * @param node to validate
	 */
	private void validateAllSubPopulations(MeasureConfig measureConfig, Node node) {
		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();

		if (subPopulations == null) {
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
				Arrays.asList(
						makeValidator(
								subPopulation::getDenominatorExceptionsUuid, "denominator exception", "DENEXCEP"),
						makeValidator(
								subPopulation::getDenominatorExclusionsUuid, "denominator exclusion", "DENEX"),
						makeValidator(subPopulation::getNumeratorUuid, "numerator", "NUMER"),
						makeValidator(
								subPopulation::getInitialPopulationUuid, "initial population", "IPOP", "IPP"),
						makeValidator(subPopulation::getDenominatorUuid, "denominator", "DENOM"));
		validations.forEach(validate -> validate.accept(node));
	}

	/**
	 * Method template for measure validations.
	 *
	 * @param check a property existence check
	 * @param keys that identify measures
	 * @param label a short measure description
	 * @return a callback / consumer that will perform a measure specific validation against a given
	 *		 node.
	 */
	private Consumer<Node> makeValidator(Supplier<Object> check, String label, String... keys) {
		return node -> {
			if (check.get() != null) {
				Predicate<Node> childFinder = makeChildFinder(check, keys);
				List<Node> childMeasureNodes = node.getChildNodes(childFinder).collect(Collectors.toList());
				if (childMeasureNodes.isEmpty()) {
					String message = String.format(REQUIRED_CHILD_MEASURE, label);
					this.getValidationErrors().add(new ValidationError(message, node.getPath()));
				}
			}
		};
	}

	/**
	 * Search filter for child measure nodes.
	 *
	 * @param check provides sub population specific measure id
	 * @param keys that identify measures
	 * @return search filter
	 */
	private Predicate<Node> makeChildFinder(Supplier<Object> check, String... keys) {
		return thisNode -> {
			boolean validMeasureType =
					(keys.length > 1)
							? Arrays.stream(keys).anyMatch(key -> key.equals(thisNode.getValue(MEASURE_TYPE)))
							: keys[0].equals(thisNode.getValue(MEASURE_TYPE));

			return validMeasureType && check.get().equals(thisNode.getValue(MEASURE_POPULATION));
		};
	}

	/**
	 * Does nothing.
	 *
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		//no cross-node validations required
	}
}
