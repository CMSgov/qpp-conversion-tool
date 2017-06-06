package gov.cms.qpp.conversion.validate;


import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * This Validator checks that exactly one Aggregate Count Child exists,
 * and that its aggregate count value is a positive integer value.
 */
@Validator(value = TemplateId.ACI_NUMERATOR, required = true)
public class AciNumeratorValidator extends CommonNumeratorDenominatorValidator {
	protected static final String NUMERATOR_NAME = "Numerator";

	/**
	 * Class that validates ACI Numerator Nodes
	 * ACI Numerator Nodes should contain exactly one AggregateCount Node
	 */
	public AciNumeratorValidator() {
		nodeName = NUMERATOR_NAME;
	}
}