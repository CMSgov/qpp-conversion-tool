package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.decode.StratifierDecoder;
import gov.cms.qpp.conversion.encode.QualityMeasureIdEncoder;
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
import gov.cms.qpp.conversion.util.NumberHelper;

import java.util.Arrays;
import java.util.List;
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
	protected void performValidation(Node node) {
		super.performValidation(node);
		MeasureConfig measureConfig = MeasureConfigHelper.getMeasureConfig(node);
		if (measureConfig != null && measureConfig.getStrata() != null) {
			int requiredPerformanceRateCount = measureConfig.getStrata().size();

			forceCheckErrors(node)
					.childExact(
						ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT
							.format(requiredPerformanceRateCount, MeasureConfigHelper.getPrioritizedId(node)),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
		}

		//Validation for the Performance Denominator. Performance denominator is Denominator - Denex - Denexcep.
		//Skips this validation if any of the measure data errors occur before this.
		if (viewErrors().isEmpty()) {
			List<Node> subPopNodes = MeasureConfigHelper.createSubPopulationGrouping(node, measureConfig);
			for (Node subpopulationNode: subPopNodes) {
				Node numeratorNode = subpopulationNode.findChildNode(
					n -> SubPopulationLabel.NUMER.hasAlias(n.getValue(QualityMeasureIdEncoder.TYPE)));
				Node denominatorNode = subpopulationNode.findChildNode(
					n -> SubPopulationLabel.DENOM.hasAlias(n.getValue(QualityMeasureIdEncoder.TYPE)));
				Node denomExclusionNode = subpopulationNode.findChildNode(
					n -> SubPopulationLabel.DENEX.hasAlias(n.getValue(QualityMeasureIdEncoder.TYPE)));
				Node denomExceptionNode = subpopulationNode.findChildNode(
					n -> SubPopulationLabel.DENEXCEP.hasAlias(n.getValue(QualityMeasureIdEncoder.TYPE)));

				Node performanceRateNode = node.getChildNodes(n ->
					TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE.equals(n.getType()))
					.filter(n -> numeratorNode.getValue(MEASURE_POPULATION).equalsIgnoreCase
						(n.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID)))
					.findFirst()
					.orElse(null);

				//skip if performance rate is missing
				if (null != performanceRateNode) {
					if (PerformanceRateValidator.NULL_ATTRIBUTE.equals(
						performanceRateNode.getValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE))) {
						int performanceDenominator =
							calculatePerformanceDenom(denominatorNode, denomExclusionNode, denomExceptionNode);
						if (performanceDenominator != 0 || extractAggregateValue(numeratorNode) != 0) {
							addError(Detail.forErrorAndNode(ErrorCode.CPC_PLUS_INVALID_NULL_PERFORMANCE_RATE, node));
						}
					}
				}

			}
		}
	}

	/**
	 * Initializes the vaidators for various sub-populations.
	 */
	@Override
	List<Consumer<Node>> prepValidations(SubPopulation subPopulation) {
		return Arrays.asList(
				makeValidator(subPopulation, subPopulation::getDenominatorExceptionsUuid, SubPopulationLabel.DENEXCEP),
				makeValidator(subPopulation, subPopulation::getDenominatorExclusionsUuid, SubPopulationLabel.DENEX),
				makeValidator(subPopulation, subPopulation::getNumeratorUuid, SubPopulationLabel.NUMER),
				makeValidator(subPopulation, subPopulation::getDenominatorUuid, SubPopulationLabel.DENOM),
				makeValidator(subPopulation, subPopulation::getInitialPopulationUuid, SubPopulationLabel.IPOP),
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
						makeUuidChildFinder(check, ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE,
								PERFORMANCE_RATE_ID);

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
			addError(Detail.forErrorAndNode(error, node));
		}

		sub.getStrata().forEach(stratum -> {
			Predicate<Node> seek = child ->
					child.getValue(StratifierDecoder.STRATIFIER_ID).equalsIgnoreCase(stratum);

			if (strataNodes.stream().noneMatch(seek)) {
				LocalizedError error = ErrorCode.CPC_QUALITY_MEASURE_ID_MISSING_STRATA.format(stratum,
						node.getValue(MeasureDataDecoder.MEASURE_TYPE),
						node.getValue(MEASURE_POPULATION));
				addError(Detail.forErrorAndNode(error, node));
			}
		});
	}

	/**
	 * calculates the Performance Denominator from the extracted aggregate values of each node
	 *
	 * @param denom node that holds the denominator aggregate count
	 * @param denex node that holds the denominator exclusion aggregate count
	 * @param denexcep node that holds the denominator exception aggregate count
	 * @return
	 */
	private Integer calculatePerformanceDenom(Node denom, Node denex, Node denexcep) {
		int denomValue = extractAggregateValue(denom);
		int denexValue = extractAggregateValue(denex);
		int denexcepValue = extractAggregateValue(denexcep);

		return denomValue - denexValue - denexcepValue;
	}

	/**
	 * Extracts the aggregate count from the node or returns 0 if not found.
	 *
	 * @param node
	 * @return
	 */
	private Integer extractAggregateValue(Node node) {
		Integer extractedValue = 0;
		if (null != node) {
			Node aggregate =
				node.getChildNodes(n -> TemplateId.PI_AGGREGATE_COUNT.equals(n.getType())).findFirst().orElse(null);
			if (aggregate != null) {
				String value = aggregate.getValue(AggregateCountDecoder.AGGREGATE_COUNT);
				if (NumberHelper.isNumeric(value)) {
					extractedValue = Integer.valueOf(value);
				}
			}
		}
		return extractedValue;
	}
}
