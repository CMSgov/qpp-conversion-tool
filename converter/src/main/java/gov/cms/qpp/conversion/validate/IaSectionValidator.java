package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates Improvement Activity Section Node - expects at least one Improvement Activity Measure
 */
@Validator(TemplateId.IA_SECTION)
public class IaSectionValidator extends NodeValidator {

	/**
	 * Validates a single IA Section node to ensure at least one Improvement Activity Measure exists
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
				.childMinimum(ErrorCode.IA_SECTION_MISSING_IA_MEASURE, 1, TemplateId.IA_MEASURE)
				.onlyHasChildren(ErrorCode.IA_SECTION_WRONG_CHILD, TemplateId.IA_MEASURE, TemplateId.REPORTING_PARAMETERS_ACT)
			    .noParentChildDuplications(ErrorCode.IA_SECTION_IA_MEASURE_DUPLICATE_REPORTING_PARAM,
				    TemplateId.REPORTING_PARAMETERS_ACT, TemplateId.IA_MEASURE);

		if (node.getChildNodes(TemplateId.REPORTING_PARAMETERS_ACT).count() > 0) {
			check(node).childExact(
				ErrorCode.IA_SECTION_ONLY_ONE_REPORTING_PARAM, 1, TemplateId.REPORTING_PARAMETERS_ACT);
		} else {
			node.getChildNodes(TemplateId.IA_MEASURE).forEach(iaMeasureNode ->
				check(iaMeasureNode).incompleteValidation().childExact(
					ErrorCode.IA_MEASURE_MISSING_REPORTING_PARAM, 1, TemplateId.REPORTING_PARAMETERS_ACT));
		}
	}
}
