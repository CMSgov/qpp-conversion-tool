package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import static gov.cms.qpp.conversion.model.Constants.*;

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
		String performanceStart = node.getValueOrDefault(PERFORMANCE_START, "");

		String performanceEnd = node.getValueOrDefault(PERFORMANCE_END,"");

		checkErrors(node)
				.singleValue(ProblemCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START,
						PERFORMANCE_START)
				.singleValue(ProblemCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END,
						PERFORMANCE_END)
				.value(ProblemCode.REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR,
						PERFORMANCE_YEAR)
				.isValidDate(ProblemCode.INVALID_PERFORMANCE_PERIOD_FORMAT.format(
					performanceStart),
					PERFORMANCE_START)
				.isValidDate(ProblemCode.INVALID_PERFORMANCE_PERIOD_FORMAT.format(performanceEnd),
					PERFORMANCE_END);
	}
}
