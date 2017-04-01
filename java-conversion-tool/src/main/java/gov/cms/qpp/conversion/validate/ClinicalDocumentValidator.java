package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

@Validator(templateId = "2.16.840.1.113883.10.20.27.1.2", required = true)
public class ClinicalDocumentValidator extends NodeValidator {

	@Override
	protected void internalValidateNode(Node node) {

		Validator thisAnnotation = this.getClass().getAnnotation(Validator.class);

		List<Node> nodes = node.findNode(thisAnnotation.templateId());

		// Most likely, this "required" validation can be moved into the
		// QrdaValidator superclass
		if (thisAnnotation.required()) {
			if (nodes.isEmpty()) {
				this.addValidationError(new ValidationError("Clinical Document Node is required"));
			}
		}

		// validate the actual Node structure
		// the Clinical Document Node is a representation of the top level
		// there should only be one
		// it should not have a parent node
		// it can have one or more ACI or IA Section Nodes

		if (nodes.size() > 1) {
			this.addValidationError(new ValidationError("Only one Clinical Document Node is allowed"));
		} else if (nodes.size() == 1) {
			Node docNode = nodes.get(0);
			List<Node> childNodes = docNode.getChildNodes();

			if (childNodes.isEmpty()) {
				this.addValidationError(new ValidationError(
						"Clinical Document Node must have at least one Aci or Ia Section Node as a child"));
			} else {
				int aciOrIaCount = 0;
				for (Node child : childNodes) {
					if (NodeType.ACI_SECTION == child.getType() || NodeType.IA_SECTION == child.getType()) {
						aciOrIaCount++;
					}
				}
				if (aciOrIaCount == 0) {
					this.addValidationError(new ValidationError(
							"Clinical Document Node must have at least one Aci or Ia Section Node as a child"));
				}
			}
		}
	}

	@Override
	protected void internalValidateNodes(final List<Node> nodes) {
	}
}
