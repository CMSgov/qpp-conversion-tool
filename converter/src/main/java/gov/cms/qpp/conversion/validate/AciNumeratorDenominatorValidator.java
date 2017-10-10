package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validate all ACI Numerator Denominator Type Measures.
 */
@Validator(TemplateId.ACI_NUMERATOR_DENOMINATOR)
public class AciNumeratorDenominatorValidator extends NodeValidator {

	protected static final String NO_PARENT_SECTION =
			"This ACI Numerator Denominator Node should have an ACI Section Node as a parent";
	protected static final String NO_MEASURE_NAME =
			"This ACI Numerator Denominator Node does not contain a measure name ID";
	protected static final String NO_NUMERATOR =
			"This ACI Numerator Denominator Node does not contain a Numerator Node child";
	public static final String TOO_MANY_NUMERATORS =
			"This ACI Numerator Denominator Node contains too many Numerator Node children";
	protected static final String NO_DENOMINATOR =
			"This ACI Numerator Denominator Node does not contain a Denominator Node child";
	protected static final String TOO_MANY_DENOMINATORS =
			"This ACI Numerator Denominator Node contains too many Denominator Node children";
	protected static final String NO_CHILDREN =
			"This ACI Numerator Denominator Node does not have any child Nodes";

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
		Checker nodeChecker = check(node).hasParent(NO_PARENT_SECTION, TemplateId.ACI_SECTION);
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
				.singleValue(NO_MEASURE_NAME, "measureId")
				.hasChildren(NO_CHILDREN)
				.childMinimum(NO_DENOMINATOR, 1, TemplateId.ACI_DENOMINATOR)
				.childMinimum(NO_NUMERATOR, 1, TemplateId.ACI_NUMERATOR)
				.childMaximum(TOO_MANY_DENOMINATORS, 1, TemplateId.ACI_DENOMINATOR)
				.childMaximum(TOO_MANY_NUMERATORS, 1, TemplateId.ACI_NUMERATOR);
	}
}
