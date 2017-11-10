package gov.cms.qpp.conversion.validate;

import com.google.common.base.Strings;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;

/**
 * Validates the Clinical Document level node for the given program: CPC+
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.CPC)
public class CpcClinicalDocumentValidator extends NodeValidator {

	/**
	 * Validates a single clinical document node
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
			check(node)
					.valueIsNotEmpty(ErrorCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS,
							ClinicalDocumentDecoder.PRACTICE_SITE_ADDR)
					.singleValue(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED,
							ClinicalDocumentDecoder.ENTITY_ID)
					.valueIsNotEmpty(ErrorCode.CPC_CLINICAL_DOCUMENT_EMPTY_APM, ClinicalDocumentDecoder.ENTITY_ID)
					.childMinimum(ErrorCode.CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED,
							1, TemplateId.MEASURE_SECTION_V2);

			validateApmEntityId(node);
	}

	private void validateApmEntityId(final Node node) {
		String apmEntityId = node.getValue(ClinicalDocumentDecoder.ENTITY_ID);

		if (Strings.isNullOrEmpty(apmEntityId)) {
			return;
		}

		if (!ApmEntityIds.idExists(apmEntityId)) {
			addValidationError(Detail.forErrorAndNode(ErrorCode.CPC_CLINICAL_DOCUMENT_INVALID_APM, node));
		}
	}
}
