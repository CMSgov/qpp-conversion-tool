package gov.cms.qpp.conversion.validate;

import static gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

/**
 * Validates a Measure Reference Results node.
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.MIPS)
public class MipsQualityMeasureIdValidator extends QualityMeasureIdValidator {

	MipsQualityMeasureIdValidator() {
		subPopulationExclusions = Sets.newHashSet("IPOP", "IPP");
	}

	/**
	 * Validates node of all criteria specified for MIPS
	 * <ul>
	 *     <li>Checks that existing performance rates are valid</li>
	 * </ul>
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		super.internalValidateSingleNode(node);

		Map<String, MeasureConfig> configurationMap = MeasureConfigs.getConfigurationMap();
		String value = node.getValue(MEASURE_ID);
		MeasureConfig measureConfig = configurationMap.get(value);

		if (measureConfig != null) {
			List<SubPopulation> subPopulations = measureConfig.getSubPopulation();
			validateExistingPerformanceRates(node, subPopulations);
		}
	}

	@Override
	List<Consumer<Node>> prepValidations(SubPopulation subPopulation) {
		return Arrays.asList(makeValidator(subPopulation, subPopulation::getDenominatorExceptionsUuid, SubPopulations.DENEXCEP),
				makeValidator(subPopulation, subPopulation::getDenominatorExclusionsUuid, SubPopulations.DENEX),
				makeValidator(subPopulation, subPopulation::getNumeratorUuid, SubPopulations.NUMER),
				makeValidator(subPopulation, subPopulation::getDenominatorUuid, SubPopulations.DENOM));
	}

	/**
	 * Validates performance rates that were decoded
	 *
	 * @param node The current parent node
	 * @param subPopulations The current sub population
	 */
	private void validateExistingPerformanceRates(Node node, List<SubPopulation> subPopulations) {
		if (subPopulations.isEmpty()) {
			return;
		}
		List<Node> performanceRateNodes = node
				.getChildNodes(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.collect(Collectors.toList());

		for (Node performanceRateNode: performanceRateNodes) {
			validatePerformanceRateUuid(node, subPopulations, performanceRateNode);
		}
	}

	/**
	 * Validates an individual performance rate
	 *
	 * @param node The current parent node
	 * @param subPopulations The current sub population
	 * @param performanceRateNode The current performance rate node
	 */
	private void validatePerformanceRateUuid(Node node, List<SubPopulation> subPopulations, Node performanceRateNode) {
		validatePerformanceRateUuidExists(performanceRateNode);

		String performanceUuid = performanceRateNode.getValue(PERFORMANCE_RATE_ID);

		if (performanceUuid != null) {
			SubPopulation subPopulation = subPopulations.stream()
					.filter(makePerformanceRateUuidFinder(performanceUuid))
					.findFirst()
					.orElse(null);

			if (subPopulation == null) {
				addPerformanceRateValidationMessage(node, performanceUuid);
			}
		}
	}

	/**
	 * Validates if the performance rate uuid exists.
	 *
	 * @param performanceRateNode The current performance rate node
	 */
	private void validatePerformanceRateUuidExists(Node performanceRateNode) {
		thoroughlyCheck(performanceRateNode)
				.incompleteValidation()
				.singleValue(ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE, PERFORMANCE_RATE_ID);
	}

	/**
	 * Creates a {@link Predicate} which takes a SubPopulation and tests whether
	 * the SubPopulation's Uuid is equal to the given unique id
	 *
	 * @param uuid
	 * @return
	 */
	private Predicate<SubPopulation> makePerformanceRateUuidFinder(String uuid) {
		return subPopulation -> uuid.equals(subPopulation.getNumeratorUuid());
	}

	/**
	 * Adds a validation error message for a specified Performance Rate
	 *
	 * @param node The current parent node of performance rate
	 * @param performanceUuid The current performance rate uuid
	 */
	private void addPerformanceRateValidationMessage(Node node, String performanceUuid) {
		MeasureConfig config =
				MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
		LocalizedError error = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(config.getElectronicMeasureId(),
				PERFORMANCE_RATE_ID, performanceUuid);
		addValidationError(Detail.forErrorCodeAndNode(error, node));
	}
}
