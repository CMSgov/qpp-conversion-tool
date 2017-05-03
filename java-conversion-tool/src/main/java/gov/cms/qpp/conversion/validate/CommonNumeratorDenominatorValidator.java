package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;

/**
 * Super class for AciNumeratorValidator and AciDenominatorValidator classes
 * Factored out common functionality
 */
public class CommonNumeratorDenominatorValidator extends NodeValidator {
	protected static final String INCORRECT_CHILD =
			"This %s Node does not have an Aggregate Count Node";
	static final String NOT_AN_INTEGER_VALUE =
			"This %s Node Aggregate Value is not an integer";
	static final String INVALID_VALUE =
			"This %s Node Aggregate Value has an invalid value";
	protected static final String NO_CHILDREN =
			"This %s Node does not have any child Nodes";
	protected static final String TOO_MANY_CHILDREN =
			"This %s Node has too many child Nodes";
	protected static final String AGGREGATE_COUNT_FIELD = "aggregateCount";

	protected String nodeName;

	/**
	 * internalValidateSameTemplateIdNodes allows for any cross node dependencies
	 * to be validated. AciNumerator does not have any cross node dependencies
	 *
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
		check(node).hasChildren(String.format(NO_CHILDREN, nodeName))
				.childMinimum(String.format(INCORRECT_CHILD, nodeName), 1, TemplateId.ACI_AGGREGATE_COUNT)
				.childMaximum(String.format(TOO_MANY_CHILDREN, nodeName), 1, TemplateId.ACI_AGGREGATE_COUNT);
		if (getValidationErrors().isEmpty()) {
			validateAggregateCount(
					node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId()));
		}
	}

	/**
	 * Common ACI numerator denominator validation for aggregate count. Marks the
	 * {@link TemplateId#ACI_AGGREGATE_COUNT} node as validated to prevent duplicate
	 * validation by the {@link AggregateCountValidator}
	 *
	 * @param aggregateCountNode aggregate count node
	 */
	private void validateAggregateCount(Node aggregateCountNode) {
		String invalidMessage = String.format(INVALID_VALUE, nodeName);
		check(aggregateCountNode)
				.value(invalidMessage, AGGREGATE_COUNT_FIELD)
				.intValue(String.format(NOT_AN_INTEGER_VALUE, nodeName), AGGREGATE_COUNT_FIELD)
				.greaterThan(invalidMessage, -1);
	}
}
