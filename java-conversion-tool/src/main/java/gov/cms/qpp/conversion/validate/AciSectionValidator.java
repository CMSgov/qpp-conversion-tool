package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

@Validator(templateId = TemplateId.ACI_SECTION, required = true)
public class AciSectionValidator extends NodeValidator {

	protected static final String ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED =
		"At least one Aci Numerator Denominator Measure Node is required";
	protected static final String NO_REQUIRED_MEASURE =
		"The required measure ''{0}'' is not present in the source file. "
			+ "Please add the ACI measure and try again.";

	@Override
	protected void internalValidateSingleNode(final Node node) {
		check(node).childMinimum(ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED, 1, TemplateId.ACI_NUMERATOR_DENOMINATOR);
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		//no cross-node validations
	}
}
