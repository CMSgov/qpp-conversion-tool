package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.decode.StratifierDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID;

/**
 * Validates a Measure Reference Results for CPC Plus requirements
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.CPC)
public class CpcQualityMeasureIdValidator extends QualityMeasureIdValidator {

	static final String INVALID_PERFORMANCE_RATE_COUNT =
			"Must contain correct number of performance rate(s). Correct Number is %s";
	static final String MISSING_STRATA = "Missing strata %s for %s measure (%s)";
	static final String STRATA_MISMATCH = "Amount of stratifications %d does not meet expectations %d "
			+ "for %s measure (%s). Expected strata: %s";

	/**
	 * Validates node of all criteria specified for CPC Plus
	 * <ul>
	 *     <li>checks that the node contains the correct number of performance rates</li>
	 * </ul>
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		super.internalValidateSingleNode(node);
		Map<String, MeasureConfig> configurationMap = MeasureConfigs.getConfigurationMap();
		String value = node.getValue(MEASURE_ID);
		MeasureConfig measureConfig = configurationMap.get(value);
		int requiredPerformanceRateCount = measureConfig.getStrata().size();

		thoroughlyCheck(node)
				.childMinimum(String.format(INVALID_PERFORMANCE_RATE_COUNT, requiredPerformanceRateCount),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.childMaximum(String.format(INVALID_PERFORMANCE_RATE_COUNT, requiredPerformanceRateCount),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);

	}

	@Override
	List<Consumer<Node>> prepValidations(SubPopulation subPopulation) {
		return Arrays.asList(
				makeValidator(subPopulation, subPopulation::getDenominatorExceptionsUuid, SubPopulations.DENEXCEP),
				makeValidator(subPopulation, subPopulation::getDenominatorExclusionsUuid, SubPopulations.DENEX),
				makeValidator(subPopulation, subPopulation::getNumeratorUuid, SubPopulations.NUMER),
				makeValidator(subPopulation, subPopulation::getDenominatorUuid, SubPopulations.DENOM),
				makeValidator(subPopulation, subPopulation::getInitialPopulationUuid,
						SubPopulations.IPOP, SubPopulations.IPP),
				makePerformanceRateUuidValidator(subPopulation::getNumeratorUuid, PERFORMANCE_RATE_ID));
	}

	/**
	 * Method for Performance Rate Uuid validations
	 *
	 * @param check a property existence check
	 * @param keys that identify measures
	 * @return a callback / consumer that will perform a measure specific validation against a given
	 * node.
	 */
	private Consumer<Node> makePerformanceRateUuidValidator(Supplier<String> check, String... keys) {
		return node -> {
			if (check.get() != null) {
				Predicate<Node> childUuidFinder =
						makeUuidChildFinder(check, SINGLE_PERFORMANCE_RATE, PERFORMANCE_RATE_ID);

				Node existingUuidChild = node
						.getChildNodes(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
						.filter(childUuidFinder)
						.findFirst()
						.orElse(null);

				if (existingUuidChild == null) {
					addMeasureConfigurationValidationMessage(check, keys, node);
				}
			}
		};
	}

	/**
	 * Validate measure strata
	 *
	 * @param node measure node
	 * @param sub sub population constituent ids
	 */
	@Override
	protected void followUpHook(Node node, SubPopulation sub) {
		List<Node> strataNodes = node.getChildNodes(TemplateId.REPORTING_STRATUM_CMS)
				.collect(Collectors.toList());

		if (strataNodes.size() != sub.getStrata().size()) {
			String message = String.format(STRATA_MISMATCH, strataNodes.size(), sub.getStrata().size(),
					node.getValue(MeasureDataDecoder.MEASURE_TYPE),
					node.getValue(MEASURE_POPULATION),
					sub.getStrata());
			this.getDetails().add(new Detail(message, node.getPath()));
		}

		sub.getStrata().forEach(stratum -> {
			Predicate<Node> seek = child ->
					child.getValue(StratifierDecoder.STRATIFIER_ID).equals(stratum);

			if (strataNodes.stream().noneMatch(seek)) {
				String message = String.format(MISSING_STRATA, stratum,
						node.getValue(MeasureDataDecoder.MEASURE_TYPE),
						node.getValue(MEASURE_POPULATION));
				this.getDetails().add(new Detail(message, node.getPath()));
			}
		});
	}

}
