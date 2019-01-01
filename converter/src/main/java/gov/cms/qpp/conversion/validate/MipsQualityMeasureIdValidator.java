package gov.cms.qpp.conversion.validate;

import com.google.common.collect.Sets;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.util.MeasureConfigHelper;
import gov.cms.qpp.conversion.util.StringHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID;

/**
 * Validates a Measure Reference Results node.
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.MIPS)
public class MipsQualityMeasureIdValidator extends QualityMeasureIdValidator {

	MipsQualityMeasureIdValidator() {
		subPopulationExclusions = Sets.newHashSet(SubPopulationLabel.IPOP);
	}

	/**
	 * Validates node of all criteria specified for MIPS
	 * <ul>
	 *     <li>Checks that existing performance rates are valid</li>
	 * </ul>
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		super.performValidation(node);
		MeasureConfig measureConfig = MeasureConfigHelper.getMeasureConfig(node);

		if (measureConfig != null) {
			validateExistingPerformanceRates(node, measureConfig);
		}
	}

	@Override
	List<Consumer<Node>> prepValidations(SubPopulation subPopulation) {
		return Arrays.asList(
			makeValidator(subPopulation, subPopulation::getDenominatorExceptionsUuid, SubPopulationLabel.DENEXCEP),
			makeValidator(subPopulation, subPopulation::getDenominatorExclusionsUuid, SubPopulationLabel.DENEX),
			makeValidator(subPopulation, subPopulation::getNumeratorUuid, SubPopulationLabel.NUMER),
			makeValidator(subPopulation, subPopulation::getDenominatorUuid, SubPopulationLabel.DENOM));
	}

	/**
	 * Validates performance rates that were decoded
	 *
	 * @param node The current parent node
	 * @param measureConfig The current sub population
	 */
	private void validateExistingPerformanceRates(Node node, MeasureConfig measureConfig) {
		if (measureConfig.getSubPopulation().isEmpty()) {
			return;
		}
		List<Node> performanceRateNodes = node
				.getChildNodes(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.collect(Collectors.toList());

		for (Node performanceRateNode: performanceRateNodes) {
			validatePerformanceRateUuid(node, measureConfig, performanceRateNode);
		}
	}

	/**
	 * Validates an individual performance rate
	 *
	 * @param node The current parent node
	 * @param measureConfig Holds the current sub population and electronic measure id
	 * @param performanceRateNode The current performance rate node
	 */
	private void validatePerformanceRateUuid(Node node, MeasureConfig measureConfig, Node performanceRateNode) {
		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();
		validatePerformanceRateUuidExists(performanceRateNode);

		String performanceUuid = performanceRateNode.getValue(PERFORMANCE_RATE_ID);

		if (performanceUuid != null) {
			SubPopulation subPopulation = subPopulations.stream()
					.filter(makePerformanceRateUuidFinder(performanceUuid))
					.findFirst()
					.orElse(null);

			if (subPopulation == null) {
				Set<String> expectedPerformanceUuids = subPopulations.stream()
					.map(SubPopulation::getNumeratorUuid)
					.collect(Collectors.toSet());
				String expectedUuidString = StringHelper.join(expectedPerformanceUuids, ",", "or");
				addPerformanceRateValidationMessage(node, measureConfig.getElectronicMeasureId(), expectedUuidString);
			}
		}
	}

	/**
	 * Validates if the performance rate uuid exists.
	 *
	 * @param performanceRateNode The current performance rate node
	 */
	private void validatePerformanceRateUuidExists(Node performanceRateNode) {
		forceCheckErrors(performanceRateNode)
				.incompleteValidation()
				.singleValue(ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE, PERFORMANCE_RATE_ID);
	}

	/**
	 * Creates a {@link Predicate} which takes a SubPopulation and tests whether
	 * the SubPopulation's Uuid is equal to the given unique id
	 *
	 * @param uuid value that forms basis for finder comparison
	 * @return finder predicate
	 */
	private Predicate<SubPopulation> makePerformanceRateUuidFinder(String uuid) {
		return subPopulation -> uuid.equalsIgnoreCase(subPopulation.getNumeratorUuid());
	}

	/**
	 * Adds a validation error message for a specified Performance Rate
	 *
	 * @param node The current parent node of performance rate
	 * @param performanceUuid The current performance rate uuid
	 */
	private void addPerformanceRateValidationMessage(Node node, String electronicMeasureId,String performanceUuid) {
		LocalizedError error = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(electronicMeasureId,
				PERFORMANCE_RATE_ID, performanceUuid);
		addError(Detail.forErrorAndNode(error, node));
	}
}
