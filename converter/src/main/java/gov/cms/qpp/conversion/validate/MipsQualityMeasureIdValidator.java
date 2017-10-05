package gov.cms.qpp.conversion.validate;

import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID;

/**
 * Validates a Measure Reference Results node.
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.MIPS)
public class MipsQualityMeasureIdValidator extends QualityMeasureIdValidator {

	MipsQualityMeasureIdValidator() {
		subPopulationExclusions = Sets.newHashSet("IPOP", "IPP");
	}

	/**
	 * Validate individual sub-populations.
	 *
	 * @param node to validate
	 * @param subPopulation a grouping of measures
	 */
	@Override
	protected void validateSubPopulation(Node node, SubPopulation subPopulation) {

		List<Consumer<Node>> validations =
			Arrays.asList(makeValidator(subPopulation, subPopulation::getDenominatorExceptionsUuid, SubPopulations.DENEXCEP),
				makeValidator(subPopulation, subPopulation::getDenominatorExclusionsUuid, SubPopulations.DENEX),
				makeValidator(subPopulation, subPopulation::getNumeratorUuid, SubPopulations.NUMER),
				makeValidator(subPopulation, subPopulation::getDenominatorUuid, SubPopulations.DENOM),
				makePerformanceRateUuidValidator(subPopulation::getNumeratorUuid, PERFORMANCE_RATE_ID));

		validations.forEach(validate -> validate.accept(node));

		validateDenomCountToIpopCount(node, subPopulation);
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
