package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;

/**
 * Validates Improvement Activity Section Node - expects at least one Improvement Activity Measure
 */
@Validator(TemplateId.IA_SECTION_V3)
public class IaSectionValidator extends NodeValidator {

	/**
	 * Validates a single IA Section node to ensure at least one Improvement Activity Measure exists
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		checkErrors(node)
				.childMinimum(ProblemCode.IA_SECTION_MISSING_IA_MEASURE, 1, TemplateId.IA_MEASURE)
				.childExact(ProblemCode.IA_SECTION_MISSING_REPORTING_PARAM, 1, TemplateId.REPORTING_PARAMETERS_ACT)
				.onlyHasChildren(ProblemCode.IA_SECTION_WRONG_CHILD, TemplateId.IA_MEASURE, TemplateId.REPORTING_PARAMETERS_ACT);
	}
}
