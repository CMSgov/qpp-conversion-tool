package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * Validates Improvement Activity Section Node - expects at least one Improvement Activity Measure
 */
@Validator(templateId = TemplateId.IA_SECTION, required = true)
public class IASectionValidator extends NodeValidator {

	public static final String MINIMIUM_REQUIREMENT_ERROR = "Must have at least one IA Measure";

	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
				.childMinimum(MINIMIUM_REQUIREMENT_ERROR, 1, TemplateId.IA_MEASURE);
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// No current cross node IA section validations
	}
}
