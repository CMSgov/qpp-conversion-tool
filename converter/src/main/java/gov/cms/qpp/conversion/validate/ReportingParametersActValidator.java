package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates REPORTING_PARAMETERS_ACT nodes.
 * (Annotation registered)
 */
@Validator(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActValidator extends NodeValidator {

	/**
	 * Validates a single Reporting Parameters Template {@link Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *    <li>both start and end exist as dates</li>
	 *    <li>and that performance year is present</li>
	 * </ul>
	 *
	 * @param node Node that represents Reporting Parameters.
	 */
	@Override
	protected void performValidation(Node node) {
		String performanceStart = node.getValueOrDefault(ReportingParametersActDecoder.PERFORMANCE_START, "");

		String performanceEnd = node.getValueOrDefault(ReportingParametersActDecoder.PERFORMANCE_END,"");

		checkErrors(node)
				.singleValue(ErrorCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START,
						ReportingParametersActDecoder.PERFORMANCE_START)
				.singleValue(ErrorCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END,
						ReportingParametersActDecoder.PERFORMANCE_END)
				.value(ErrorCode.REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR,
						ReportingParametersActDecoder.PERFORMANCE_YEAR)
				.isValidDate(ErrorCode.INVALID_PERFORMANCE_PERIOD_FORMAT.format(
					performanceStart),
					ReportingParametersActDecoder.PERFORMANCE_START)
				.isValidDate(ErrorCode.INVALID_PERFORMANCE_PERIOD_FORMAT.format(performanceEnd),
					ReportingParametersActDecoder.PERFORMANCE_END);
	}
}
