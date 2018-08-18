package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;

import java.util.List;

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
				.childExact(ErrorCode.IA_SECTION_MISSING_REPORTING_PARAM, 1, TemplateId.REPORTING_PARAMETERS_ACT)
				.onlyHasChildren(ErrorCode.IA_SECTION_WRONG_CHILD, TemplateId.IA_MEASURE, TemplateId.REPORTING_PARAMETERS_ACT);

		if (node.getChildNodes(TemplateId.REPORTING_PARAMETERS_ACT).count() > 0) {
			node.getChildNodes(TemplateId.IA_MEASURE).forEach(child -> {
				if (child.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT) != null) {
					//add error
					addValidationError(Detail.forErrorAndNode(ErrorCode.IA_SECTION_MISSING_REPORTING_PARAM, node));
				}
			});
		} else {
			check(node).childExact(ErrorCode.IA_SECTION_MISSING_REPORTING_PARAM, 1, TemplateId.REPORTING_PARAMETERS_ACT);
		}
	}
	// if has reporting param here, check children...
	//   - if children also have reporting param fail.
	//   - if children don't have reporting param pass
	// else if reporting param non existent
	//   - check if all measure have reporting param individually
	//      - pass if so fail otherwise
}
