package gov.cms.qpp.conversion.validate;

/**
 * This Validator checks that exactly one Aggregate Count Child exists,
 * and that its aggregate count value is a positive integer value.
 */

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

@Validator(templateId = "2.16.840.1.113883.10.20.27.3.31", required = true)
public class AciNumeratorValidator extends AciNumeratorDenominatorValidator {

  /**
  Class that validates ACI Numerator Nodes
  */
	public AciNumeratorValidator() {
		nodeName = "Numerator";
	}
}