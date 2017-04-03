package gov.cms.qpp.conversion.validate;

import java.util.List;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

@Validator(templateId = "2.16.840.1.113883.10.20.27.1.2", required = true)
public class ClinicalDocumentValidator extends QrdaValidator {

	public static final String CLINICAL_DOCUMENT_REQUIRED = "Clinical Document Node is required";
	public static final String EXACTLY_ONE_DOCUMENT_ALLOWED = "Only one Clinical Document Node is allowed";
	public static final String ONE_CHILD_REQUIRED = "Clinical Document Node must have at least one Aci or Ia Section Node as a child";

	/**
	 * ClinicalDocumentValidator validates the list of Nodes that were decoded
	 *
	 * @param node Node the decodes list of nodes
	 * @return List<ValidationError>
	 */
	@Override
	protected List<ValidationError> internalValidate(Node node) {

		Validator thisAnnotation = this.getClass().getAnnotation(Validator.class);

		List<Node> nodes = node.findNode(thisAnnotation.templateId());

		if (thisAnnotation.required() && nodes.isEmpty()) {
			this.addValidationError(new ValidationError(CLINICAL_DOCUMENT_REQUIRED));
		}

		validateChildren(nodes);

		return this.getValidationErrors();
	}

	private void validateChildren(List<Node> nodes) {
		if (nodes.size() > 1) {
			this.addValidationError(new ValidationError(EXACTLY_ONE_DOCUMENT_ALLOWED));
		} else if (nodes.size() == 1) {
			Node docNode = nodes.get(0);
			List<Node> childNodes = docNode.getChildNodes();

			if (childNodes.isEmpty()) {
				this.addValidationError(new ValidationError(
						ONE_CHILD_REQUIRED));
			} else {
				int aciOrIaCount = 0;
				for (Node child : childNodes) {
					if (NodeType.ACI_SECTION == child.getType() || NodeType.IA_SECTION == child.getType()) {
						aciOrIaCount++;
					}
				}
				if (aciOrIaCount == 0) {
					this.addValidationError(new ValidationError(
							ONE_CHILD_REQUIRED));
				}
			}
		}
	}

}
