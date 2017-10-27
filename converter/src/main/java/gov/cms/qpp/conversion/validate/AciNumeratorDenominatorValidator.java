package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validate all ACI Numerator Denominator Type Measures.
 */
@Validator(TemplateId.ACI_NUMERATOR_DENOMINATOR)
public class AciNumeratorDenominatorValidator extends NodeValidator {

	/**
	 * Validates a single ACI Numerator Denominator Type Measure.
	 * <p>
	 * Validates the following.
	 * <ul>
	 * <li>ACI Numerator Denominator Type Measure nodes have an ACI section as a parent.</li>
	 * <li>ACI Numerator Denominator Type Measure nodes have one and only one numerator node.</li>
	 * <li>ACI Numerator Denominator Type Measure nodes have one and only one denominator node.</li>
	 * </ul>
	 *
	 * @param node The node that represents an ACI Numerator Denominator Type Measure.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {

		//the aci numerator denominator measure node must have an aci section node as parent
		Checker nodeChecker = check(node).hasParent(ErrorCode.ACI_NUMERATOR_DENOMINATOR_PARENT_NOT_ACI_SECTION, TemplateId.ACI_SECTION);
		//the aci numerator denominator measure node must have a numerator node and a denominator node as children
		validateChildren(nodeChecker);
	}

	/**
	 * Validates all of the given nodes children.
	 *
	 * @param nodeChecker for a node that represents the ACI Numerator Denominator Measure Section
	 */
	private void validateChildren(Checker nodeChecker) {
		nodeChecker
				.singleValue(ErrorCode.ACI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID, "measureId")
				.hasChildren(ErrorCode.ACI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN)
				.childMinimum(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_DENOMINATOR_CHILD_NODE, 1, TemplateId.ACI_DENOMINATOR)
				.childMinimum(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_NUMERATOR_CHILD_NODE, 1, TemplateId.ACI_NUMERATOR)
				.childMaximum(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_DENOMINATORS, 1, TemplateId.ACI_DENOMINATOR)
				.childMaximum(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_NUMERATORS, 1, TemplateId.ACI_NUMERATOR);
	}
}
