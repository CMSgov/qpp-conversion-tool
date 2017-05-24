package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ValidationError;

import java.util.List;

/**
 * Validates the Clinical Document.
 */
@Validator(templateId = TemplateId.CLINICAL_DOCUMENT, required = true)
public class ClinicalDocumentValidator extends NodeValidator {

	protected static final String CLINICAL_DOCUMENT_REQUIRED = "Clinical Document Node is required";
	protected static final String EXACTLY_ONE_DOCUMENT_ALLOWED = "Only one Clinical Document Node is allowed";
	protected static final String ONE_CHILD_REQUIRED = "Clinical Document Node must have at least one Aci "
			+ "or IA or eCQM Section Node as a child";
	protected static final String CONTAINS_PROGRAM_NAME = "Clinical Document must have a program name";
	protected static final String INCORRECT_PROGRAM_NAME = "Clinical Document program name is not recognized";
	protected static final String CONTAINS_PERFORMANCE_YEAR = "Clinical Document must have a performance year";
	protected static final String CONTAINS_TAX_ID_NUMBER = "Clinical Document must have Tax Id Number (TIN)";
	protected static final String CONTAINS_DUPLICATE_ACI_SECTIONS = "Clinical Document contains duplicate ACI sections";
	protected static final String CONTAINS_DUPLICATE_IA_SECTIONS = "Clinical Document contains duplicate IA sections";
	protected static final String CONTAINS_DUPLICATE_ECQM_SECTIONS = "Clinical Document contains duplicate eCQM sections";
	protected static final String REPORTING_PARAMETER_REQUIRED = "Clinical Document must have Report Parameters Section";

	/**
	 * Validates a single Clinical Document Node.
	 * Validates the following.
	 * <ul>
	 * <li>At least one child exists.</li>
	 * <li>At least one ACI or IA or eCQM (MEASURE_SECTION_V2) section exists.</li>
	 * <li>Program name is required</li>
	 * <li>TIN name is required</li>
	 * <li>Performance year is required</li>
	 * </ul>
	 * </p>
	 *
	 * @param node Node that represents a Clinical Document.
	 */
	@Override
	protected void internalValidateSingleNode(final Node node) {
		thoroughlyCheck(node)
			.hasChildren(ONE_CHILD_REQUIRED)
			.childMinimum(ONE_CHILD_REQUIRED, 1, TemplateId.ACI_SECTION, TemplateId.IA_SECTION, TemplateId.MEASURE_SECTION_V2)
			.childMinimum(REPORTING_PARAMETER_REQUIRED, 1, TemplateId.REPORTING_PARAMETERS_SECTION)
			.childMaximum(CONTAINS_DUPLICATE_ACI_SECTIONS, 1, TemplateId.ACI_SECTION)
			.childMaximum(CONTAINS_DUPLICATE_IA_SECTIONS, 1, TemplateId.IA_SECTION)
			.childMaximum(CONTAINS_DUPLICATE_ECQM_SECTIONS, 1, TemplateId.MEASURE_SECTION_V2)
			.value(CONTAINS_PROGRAM_NAME, ClinicalDocumentDecoder.PROGRAM_NAME)
			.valueIn(INCORRECT_PROGRAM_NAME, ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME)
			.value(CONTAINS_TAX_ID_NUMBER, MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);

		Node reportingParametersAct = node.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId());
		if (reportingParametersAct == null) {
			getValidationErrors().add(new ValidationError(CONTAINS_PERFORMANCE_YEAR, node.getPath()));
		} else {
			thoroughlyCheck(reportingParametersAct).value(CONTAINS_PERFORMANCE_YEAR, "performanceStart");
		}
	}

	/**
	 * Validates all the Clinical Documents together.
	 * <p>
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
