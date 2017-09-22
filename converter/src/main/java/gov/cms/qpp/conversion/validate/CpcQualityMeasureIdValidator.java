package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import java.util.Map;

/**
 * Validates a Measure Reference Results for CPC Plus requirements
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.CPC)
public class CpcQualityMeasureIdValidator extends NodeValidator {

	protected static final String INVALID_PERFORMANCE_RATE_COUNT =
			"Must contain correct number of performance rate(s). Correct Number is %s";

	/**
	 * Validates node of all criteria specified for CPC Plus
	 * <ul>
	 *     <li>checks that the node contains the correct number of performance rates</li>
	 * </ul>
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		Map<String, MeasureConfig> configurationMap = MeasureConfigs.getConfigurationMap();
		String value = node.getValue(QualityMeasureIdValidator.MEASURE_ID);
		MeasureConfig measureConfig = configurationMap.get(value);
		int requiredPerformanceRateCount = measureConfig.getStrata().size();

		check(node)
				.childMinimum(String.format(INVALID_PERFORMANCE_RATE_COUNT, requiredPerformanceRateCount),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.childMaximum(String.format(INVALID_PERFORMANCE_RATE_COUNT, requiredPerformanceRateCount),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
	}

}
