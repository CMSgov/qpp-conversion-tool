package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;

import java.util.List;

/**
 * Super class for AciNumeratorValidator and AciDenominatorValidator classes
 * Factored out common functionality
 */
public class CommonNumeratorDenominatorValidator extends NodeValidator {

	protected static String nodeName;
	protected static final String EMPTY_MISSING_XML =
			"ACI %s Node Aggregate is empty or missing";
	protected static final String INCORRECT_CHILD =
			"This %s Node does not have an Aggregate Count Node";
	protected static final String INVALID_VALUE =
			"This ACI %s Node Aggregate Value has an invalid value %s";
	protected static final String NO_CHILDREN =
			"This ACI %s Node does not have any child Nodes";
	protected static final String TOO_MANY_CHILDREN =
			"This ACI %s Node has too many child Nodes";
	protected static final String DENOMINATOR_CANNOT_BE_ZERO =
			"The ACI Denominator's Aggregate Value can not be zero";

	/**
	 * internalValidateSameTemplateIdNodes allows for any cross node dependencies
	 * to be validated. AciNumerator does not have any cross node dependencies
	 * @param nodes List of Node
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// no cross-node ACI Numerator validations required
	}

	/**
	 * internalValidateSingleNode inspects the node for certain validations.
	 * Will add validation errors if any exist
	 *
	 * @param node Node parsed xml fragment under consideration
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		if (node == null) {
			this.addValidationError(new ValidationError(String.format(EMPTY_MISSING_XML, nodeName)));
			return;
		}
		List<Node> children = node.getChildNodes();

		if (children.isEmpty()) {
			this.addValidationError(new ValidationError(String.format(NO_CHILDREN, nodeName), node.getPath()));
			return;
		}
		Node child = children.get(0);
		if (TemplateId.ACI_AGGREGATE_COUNT != child.getType()) {
			this.addValidationError(new ValidationError(String.format(INCORRECT_CHILD, nodeName), node.getPath()));
			return;
		}
		if (children.size() > 1) {
			this.addValidationError(new ValidationError(String.format(TOO_MANY_CHILDREN, nodeName), node.getPath()));
			return;
		}
		String value = child.getValue("aggregateCount");
		try {
			int val = Integer.parseInt(value);
			if (val < 0) {
				this.addValidationError(
						new ValidationError(String.format(INVALID_VALUE, nodeName, value), child.getPath()));
			}
			if (AciDenominatorValidator.DENOMINATOR_NAME.equals(nodeName) && val == 0) {
				this.addValidationError(
						new ValidationError(DENOMINATOR_CANNOT_BE_ZERO, child.getPath()));
			}
		} catch (NumberFormatException nfe) {
			//no validation error required due to this being caught by the Aggregate Count validator
		}
	}
}