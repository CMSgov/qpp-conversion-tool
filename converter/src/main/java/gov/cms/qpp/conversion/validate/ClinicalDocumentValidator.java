package gov.cms.qpp.conversion.validate;

import java.util.Optional;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.util.StringHelper;

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
	 * <li>At least one ACI or IA or eCQM (CATEGORY_SECTION_V4) section exists.</li>
	 * <li>Program name is required</li>
	 * <li>TIN name is required</li>
	 * <li>Performance year is required</li>
	 * </ul>
	 *
	 * @param node Node that represents a Clinical Document.
	 */
	@Override
	protected void performValidation(final Node node) {

		forceCheckErrors(node)
			.childMinimum(ProblemCode.CLINICAL_DOCUMENT_MISSING_PI_OR_IA_OR_ECQM_CHILD, 1,
					TemplateId.PI_SECTION_V2, TemplateId.IA_SECTION, TemplateId.MEASURE_SECTION_V4)
			.childMaximum(ProblemCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_PI_SECTIONS, 1,
					TemplateId.PI_SECTION_V2)
			.childMaximum(ProblemCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS, 1,
					TemplateId.IA_SECTION)
			.childMaximum(ProblemCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS, 1,
					TemplateId.MEASURE_SECTION_V4)
			.singleValue(ProblemCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(VALID_PROGRAM_NAMES),
					ClinicalDocumentDecoder.PROGRAM_NAME);

		if (!containsError(Detail.forProblemAndNode(ProblemCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(VALID_PROGRAM_NAMES), node))) {
			String programName = Optional.ofNullable(node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME)).orElse("<missing>");
			String entityType = Optional.ofNullable(node.getValue(ClinicalDocumentDecoder.ENTITY_TYPE)).orElse("<missing>");

			forceCheckErrors(node).valueIn(ProblemCode.CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME.format(programName, VALID_PROGRAM_NAMES),
				ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME, ClinicalDocumentDecoder.PCF, ClinicalDocumentDecoder.APP_PROGRAM_NAME);

		}
	}
}
