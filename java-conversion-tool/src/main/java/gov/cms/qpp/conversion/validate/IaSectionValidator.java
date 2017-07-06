package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates Improvement Activity Section Node - expects at least one Improvement Activity Measure
 */
@Validator(value = TemplateId.IA_SECTION, required = true)
public class IaSectionValidator extends NodeValidator {

	public static final String MINIMUM_REQUIREMENT_ERROR = "The IA Section must have at least one IA Measure";
	public static final String REPORTING_PARAM_REQUIREMENT_ERROR
			= "The IA Section must have one Reporting Parameter ACT";
	public static final String WRONG_CHILD_ERROR =
			"The IA Section must contain only measures and reporting parameter";

	/**
	 * Validates a single IA Section node to ensure at least one Improvement Activity Measure exists
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
				.childMinimum(MINIMUM_REQUIREMENT_ERROR, 1, TemplateId.IA_MEASURE)
				.childMinimum(REPORTING_PARAM_REQUIREMENT_ERROR, 1,
						TemplateId.REPORTING_PARAMETERS_ACT)
				.childMaximum(REPORTING_PARAM_REQUIREMENT_ERROR, 1,
						TemplateId.REPORTING_PARAMETERS_ACT)
				.onlyHasChildren(WRONG_CHILD_ERROR, TemplateId.IA_MEASURE, TemplateId.REPORTING_PARAMETERS_ACT);
	}
}
