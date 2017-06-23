package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the Clinical Document.
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, required = true)
public class ClinicalDocumentValidator extends NodeValidator {

	protected static final String ONE_CHILD_REQUIRED = "Clinical Document Node must have at least one Aci "
			+ "or IA or eCQM Section Node as a child";
	public static final String CONTAINS_PROGRAM_NAME = "Clinical Document must have one and only one program name";
	public static final String INCORRECT_PROGRAM_NAME = "Clinical Document program name is not recognized";
	public static final String CONTAINS_TAX_ID_NUMBER =
			"Clinical Document must have one and only one Tax Id Number (TIN)";
	protected static final String CONTAINS_DUPLICATE_ACI_SECTIONS = "Clinical Document contains duplicate ACI sections";
	protected static final String CONTAINS_DUPLICATE_IA_SECTIONS = "Clinical Document contains duplicate IA sections";
	protected static final String CONTAINS_DUPLICATE_ECQM_SECTIONS = "Clinical Document contains duplicate eCQM sections";

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
			.childMinimum(ONE_CHILD_REQUIRED, 1, TemplateId.ACI_SECTION, TemplateId.IA_SECTION, TemplateId.MEASURE_SECTION_V2)
			.childMaximum(CONTAINS_DUPLICATE_ACI_SECTIONS, 1, TemplateId.ACI_SECTION)
			.childMaximum(CONTAINS_DUPLICATE_IA_SECTIONS, 1, TemplateId.IA_SECTION)
			.childMaximum(CONTAINS_DUPLICATE_ECQM_SECTIONS, 1, TemplateId.MEASURE_SECTION_V2)
			.singleValue(CONTAINS_PROGRAM_NAME, ClinicalDocumentDecoder.PROGRAM_NAME)
			.valueIn(INCORRECT_PROGRAM_NAME, ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME)
			.singleValue(CONTAINS_TAX_ID_NUMBER, MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);
	}
}
