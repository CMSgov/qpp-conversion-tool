package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * This Validator checks that exactly one Aggregate Count Child exists,
 * and that its aggregate count value is a positive integer value greater than zero.
 */
@Validator(TemplateId.ACI_DENOMINATOR)
public class AciDenominatorValidator extends CommonNumeratorDenominatorValidator {
	protected static final String DENOMINATOR_NAME = "Denominator";

	/**
	 * Public constructor sets the node name for this class
	 */
	public AciDenominatorValidator() {
		nodeName = DENOMINATOR_NAME;
	}
}