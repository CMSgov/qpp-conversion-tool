package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * Validates the Clinical Document.
 */
@Validator(templateId = TemplateId.CLINICAL_DOCUMENT, required = true)
public class ClinicalDocumentValidator extends NodeValidator {

	public static final String CLINICAL_DOCUMENT_REQUIRED = "Clinical Document Node is required";
	public static final String EXACTLY_ONE_DOCUMENT_ALLOWED = "Only one Clinical Document Node is allowed";
	public static final String ONE_CHILD_REQUIRED = "Clinical Document Node must have at least one Aci "
			+ "or Ia Section Node as a child";

	/**
	 * Validates a single Clinical Document {@link gov.cms.qpp.conversion.model.Node}.
	 *
	 * <p>
	 * Validates the following.
	 * <ul>
	 *     <li>At least one child exists.</li>
	 *     <li>At least one ACI or IA section exists.</li>
	 * </ul>
	 * </p>
	 *
	 * @param node Node that represents a Clinical Document.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.hasChildren(ONE_CHILD_REQUIRED)
			.childMinimum(ONE_CHILD_REQUIRED, 1, TemplateId.ACI_SECTION, TemplateId.IA_SECTION);
	}

	/**
	 * Validates all the Clinical Documents together.
	 *
	 * <p>
	 * Validates that one and only one Clinical Document {@link gov.cms.qpp.conversion.model.Node} exists.
	 * </p>
	 *
	 * @param nodes The list of all the Clinical Documents nodes.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		if (nodes.isEmpty()) {
			this.addValidationError(new ValidationError(CLINICAL_DOCUMENT_REQUIRED));
		} else if (nodes.size() > 1) {
			this.addValidationError(new ValidationError(EXACTLY_ONE_DOCUMENT_ALLOWED));
		}
	}
}
