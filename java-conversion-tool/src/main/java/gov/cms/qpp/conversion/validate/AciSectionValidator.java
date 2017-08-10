package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validate the ACI Section.
 */
@Validator(value = TemplateId.ACI_SECTION, required = true)
public class AciSectionValidator extends NodeValidator {

	protected static final String MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR
			= "The ACI Section must have one Reporting Parameter ACT";

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
				.childMinimum(MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR, 1,
						TemplateId.REPORTING_PARAMETERS_ACT)
				.childMaximum(MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR, 1,
						TemplateId.REPORTING_PARAMETERS_ACT);
	}
}
