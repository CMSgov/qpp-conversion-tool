package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

@Validator(templateId = TemplateId.ACI_SECTION, required = true)
public class AciSectionValidator extends NodeValidator {


	@Override
	protected void internalValidateSingleNode(final Node node) {

	}

	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		//no cross-node validations
	}
}
