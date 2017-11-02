package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validate the ACI Section.
 */
@Validator(TemplateId.ACI_SECTION)
public class AciSectionValidator extends NodeValidator {

	/**
	 * Validates the ACI Section.
	 * <p>
	 * Validates the following.
	 * <ul>
	 * <li>One and only one reporting parameter exists.</li>
	 * </ul>
	 *
	 * @param node An ACI section node.
	 */
	@Override
	protected void internalValidateSingleNode(final Node node) {
		thoroughlyCheck(node)
				.childMinimum(ErrorCode.ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT, 1,
						TemplateId.REPORTING_PARAMETERS_ACT)
				.childMaximum(ErrorCode.ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT, 1,
						TemplateId.REPORTING_PARAMETERS_ACT);
	}
}
