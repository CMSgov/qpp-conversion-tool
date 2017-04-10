package gov.cms.qpp.conversion.validate;


import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * This Validator checks that exactly one Aggregate Count Child exists,
 * and that its aggregate count value is a positive integer value greater than zero.
 */
@Validator(templateId = "2.16.840.1.113883.10.20.27.3.32", required = true)
public class AciDenominatorValidator extends AciNumeratorDenominatorValidator {
	protected static final String DENOMINATOR_NAME = "Denominator";
	public AciDenominatorValidator() {
		nodeName = DENOMINATOR_NAME;
	}
}