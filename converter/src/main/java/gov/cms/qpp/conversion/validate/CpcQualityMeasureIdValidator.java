package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.decode.StratifierDecoder;
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
				.childMinimum(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT.format(requiredPerformanceRateCount),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.childMaximum(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT.format(requiredPerformanceRateCount),
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
						makeUuidChildFinder(check, ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE, PERFORMANCE_RATE_ID);

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
			LocalizedError error = ErrorCode.CPC_QUALITY_MEASURE_ID_STRATA_MISMATCH.format(strataNodes.size(),
					sub.getStrata().size(),
					node.getValue(MeasureDataDecoder.MEASURE_TYPE),
					node.getValue(MEASURE_POPULATION),
					sub.getStrata());
			addValidationError(Detail.forErrorAndNode(error, node));
		}

		sub.getStrata().forEach(stratum -> {
			Predicate<Node> seek = child ->
					child.getValue(StratifierDecoder.STRATIFIER_ID).equals(stratum);

			if (strataNodes.stream().noneMatch(seek)) {
				LocalizedError error = ErrorCode.CPC_QUALITY_MEASURE_ID_MISSING_STRATA.format(stratum,
						node.getValue(MeasureDataDecoder.MEASURE_TYPE),
						node.getValue(MEASURE_POPULATION));
				addValidationError(Detail.forErrorAndNode(error, node));
			}
		});
	}

}
