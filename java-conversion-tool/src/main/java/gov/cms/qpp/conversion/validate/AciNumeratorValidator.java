package gov.cms.qpp.conversion.validate;

/**
This Validator checks that exactly one Aggregate Count Child exists.
 */

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

@Validator( templateId = "2.16.840.1.113883.10.20.27.3.3", required = true)
public class AciNumeratorValidator extends NodeValidator {

	protected static final String NO_CHILDREN = "This ACI Numerator Node does not have any child Nodes";
	protected static final String INCORRECT_CHILD = "This Numerator Node does not have an Aggregate Count Node";
	protected static final String TOO_MANY_CHILDREN = "This ACI Numerator Node has too many child Nodes";
	protected static final String INVALID_VALUE = "This ACI Numerator Node Aggregate Value has an invalid value ";

	@Override
	protected void internalValidateSingleNode(Node node) {
		//the aci proportion measure node must have a numerator node and a denominator node as children
		validateChildren(node);
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {

	}

	private void validateChildren(final Node node) {

		List<Node> children = node.getChildNodes();

		if (!children.isEmpty()) {
			if (children.size() == 1) {
				Node child = children.get(0);
				if (NodeType.ACI_AGGREGATE_COUNT != child.getType()) {
					this.addValidationError(new ValidationError(INCORRECT_CHILD));
				}else {
					String value = child.getValue("value");
					try {
						int val = Integer.parseInt(value);
					}catch(NullPointerException | IllegalArgumentException  nfe){
						this.addValidationError(
								new ValidationError(INVALID_VALUE +  value));
					}
				}
			} else {
				this.addValidationError(new ValidationError(TOO_MANY_CHILDREN));
			}
		}else {
				this.addValidationError(new ValidationError(NO_CHILDREN));
		}
	}
}