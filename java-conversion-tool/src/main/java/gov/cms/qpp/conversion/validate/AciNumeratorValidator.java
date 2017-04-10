package gov.cms.qpp.conversion.validate;


import gov.cms.qpp.conversion.model.Validator;

/**
 * This Validator checks that exactly one Aggregate Count Child exists,
 * and that its aggregate count value is a positive integer value.
 */
@Validator(templateId = "2.16.840.1.113883.10.20.27.3.31", required = true)
public class AciNumeratorValidator extends AciNumeratorDenominatorValidator {
	protected static final String NUMERATOR_NAME = "Numerator";
  /**
  Class that validates ACI Numerator Nodes
  ACI Numerator Nodes should contain exactly one AggregateCount Node
  */
	public AciNumeratorValidator() {
		nodeName = NUMERATOR_NAME;
	}
}