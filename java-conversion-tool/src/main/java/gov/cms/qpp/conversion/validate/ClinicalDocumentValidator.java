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

	protected  static final String CLINICAL_DOCUMENT_REQUIRED = "Clinical Document Node is required";
	protected static final String EXACTLY_ONE_DOCUMENT_ALLOWED = "Only one Clinical Document Node is allowed";
	protected static final String ONE_CHILD_REQUIRED = "Clinical Document Node must have at least one Aci "
			+ "or IA or eCQM Section Node as a child";
	protected static final String CONTAINS_PROGRAM_NAME = "Clinical Document must have a program name";
	protected static final String CONTAINS_PERFORMANCE_YEAR = "Clinical Document must have a performance year";
	protected static final String CONTAINS_TAX_ID_NUMBER = "Clinical Document must have Tax Id Number (TIN)";

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
			.childMinimum(ONE_CHILD_REQUIRED, 1,
					TemplateId.ACI_SECTION, TemplateId.IA_SECTION,
					TemplateId.MEASURE_SECTION_V2)
			.childMinimum(ONE_CHILD_REQUIRED, 1, TemplateId.REPORTING_PARAMETERS_SECTION)
			.value(CONTAINS_PROGRAM_NAME, "programName")
			.value(CONTAINS_TAX_ID_NUMBER, "taxpayerIdentificationNumber");
		Node reportingParametersAct = node.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId());
		if ( reportingParametersAct == null ){
			getValidationErrors().add(new ValidationError(CONTAINS_PERFORMANCE_YEAR,node.getPath()));
		} else {
			check(reportingParametersAct).value(CONTAINS_PERFORMANCE_YEAR, "performanceStart");
		}
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
