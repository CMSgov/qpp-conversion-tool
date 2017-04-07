package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * Validates the Clinical Document.
 */
@Validator(templateId = "2.16.840.1.113883.10.20.27.1.2", required = true)
public class ClinicalDocumentValidator extends NodeValidator {

	public static final String CLINICAL_DOCUMENT_REQUIRED = "Clinical Document Node is required";
	public static final String EXACTLY_ONE_DOCUMENT_ALLOWED = "Only one Clinical Document Node is allowed";
	public static final String ONE_CHILD_REQUIRED = "Clinical Document Node must have at least one Aci or Ia Section Node as a child";

	/**
	 * Validates a single Clinical Document {@link gov.cms.qpp.conversion.model.Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *     <li>At least one child exists.</li>
	 *     <li>At least one ACI or IA section exists.</li>
	 * </ul>
	 *
	 * @param node Node that represents a Clinical Document.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {

		List<Node> childNodes = node.getChildNodes();

		if (childNodes.isEmpty()) {
			this.addValidationError(new ValidationError(ONE_CHILD_REQUIRED));
		} else {
			int aciOrIaCount = 0;
			for (Node child : childNodes) {
				aciOrIaCount += (TemplateId.ACI_SECTION == child.getType() || TemplateId.IA_SECTION == child.getType()) ? 1 : 0 ;
			}
			if (aciOrIaCount == 0) {
				this.addValidationError(new ValidationError(ONE_CHILD_REQUIRED));
			}
		}
	}

	/**
	 * Validates all the Clinical Documents together.
	 *
	 * Validates that one and only one Clinical Document {@link gov.cms.qpp.conversion.model.Node} exists.
	 *
	 * @param nodes The list of all the Clinical Documents nodes.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {

		// there should only be one

		if (nodes.isEmpty()) {
			this.addValidationError(new ValidationError(CLINICAL_DOCUMENT_REQUIRED));
		}
		else if (nodes.size() > 1) {
			this.addValidationError(new ValidationError(EXACTLY_ONE_DOCUMENT_ALLOWED));
		}
	}
}
