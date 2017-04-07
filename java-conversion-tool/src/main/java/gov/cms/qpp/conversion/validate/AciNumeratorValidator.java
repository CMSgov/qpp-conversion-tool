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
public class AciNumeratorValidator extends NodeValidator {

	protected static final String EMPTY_MISSING_XML = "ACI Numerator Node Aggregate is empty or missing";
	protected static final String INCORRECT_CHILD = "This Numerator Node does not have an Aggregate Count Node  \n\t%s";
	protected static final String INVALID_VALUE = "This ACI Numerator Node Aggregate Value has an invalid value %s  \n\t%s";
	protected static final String NO_CHILDREN = "This ACI Numerator Node does not have any child Nodes  \n\t%s";
	protected static final String TOO_MANY_CHILDREN = "This ACI Numerator Node has too many child Nodes  \n\t%s";

	/**
	 * internalValidateSameTemplateIdNodes allows for any cross node dependencies
	 * to be validated. AciNumerator does not have any cross node dependencies
	 * @param nodes List of Node
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		//no cross-node ACI Numerator validations required
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
			this.addValidationError(new ValidationError(EMPTY_MISSING_XML));
			return;
		}
		List<Node> children = node.getChildNodes();

		if (children.isEmpty()) {
			this.addValidationError(new ValidationError(String.format(NO_CHILDREN, node.toString())));
			return;
		}
		if (children.size() > 1) {
			this.addValidationError(new ValidationError( String.format(TOO_MANY_CHILDREN, node.toString())));
			return;
		}
		
		Node child = children.get(0);
		if (NodeType.ACI_AGGREGATE_COUNT != child.getType()) {
			this.addValidationError(new ValidationError(String.format(INCORRECT_CHILD, node.toString())));
			return;
		}

		String value = child.getValue("aggregateCount");
		try {
			int val = Integer.parseInt(value);
			if (val < 0) {
				this.addValidationError(
						new ValidationError(String.format(INVALID_VALUE, value, node.toString())));
			}
		} catch (NumberFormatException nfe) {
			this.addValidationError(
					new ValidationError(String.format(INVALID_VALUE, value, node.toString())));
		}
	}
}