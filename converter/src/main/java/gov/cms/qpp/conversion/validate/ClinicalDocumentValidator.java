package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.util.StringHelper;

import java.util.Optional;

/**
 * Validates the Clinical Document.
 */
@Validator(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentValidator extends NodeValidator {

	public static final String VALID_PROGRAM_NAMES = StringHelper.join(Program.setOfAliases(), ",", "or");

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
	 *
	 * @param node Node that represents a Clinical Document.
	 */
	@Override
	protected void internalValidateSingleNode(final Node node) {

		thoroughlyCheck(node)
			.childMinimum(ErrorCode.CLINICAL_DOCUMENT_MISSING_ACI_OR_IA_OR_ECQM_CHILD, 1, 
					TemplateId.ACI_SECTION, TemplateId.IA_SECTION, TemplateId.MEASURE_SECTION_V2)
			.childMaximum(ErrorCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ACI_SECTIONS, 1, 
					TemplateId.ACI_SECTION)
			.childMaximum(ErrorCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS, 1, 
					TemplateId.IA_SECTION)
			.childMaximum(ErrorCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS, 1, 
					TemplateId.MEASURE_SECTION_V2)
			.singleValue(ErrorCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(VALID_PROGRAM_NAMES),
					ClinicalDocumentDecoder.PROGRAM_NAME);

		if (!getDetails().contains(
			Detail.forErrorAndNode(ErrorCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(VALID_PROGRAM_NAMES), node))) {
			String programName = Optional.ofNullable(node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME)).orElse("<missing>");

			thoroughlyCheck(node).valueIn(ErrorCode.CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME.format(programName, VALID_PROGRAM_NAMES),
				ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		}
	}
}
