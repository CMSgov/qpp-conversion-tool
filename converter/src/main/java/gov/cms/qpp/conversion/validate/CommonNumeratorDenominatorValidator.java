package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;

/**
 * Super class for AciNumeratorValidator and AciDenominatorValidator classes
 * Factored out common functionality
 */
public class CommonNumeratorDenominatorValidator extends NodeValidator {

	protected static final String AGGREGATE_COUNT_FIELD = "aggregateCount";

	protected String nodeName;

	/**
	 * internalValidateSingleNode inspects the node for certain validations.
	 * Will add validation errors if any exist
	 *
	 * @param node Node parsed xml fragment under consideration
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node).hasChildren(format(ErrorCode.NUMERATOR_DENOMINATOR_MISSING_CHILDREN))
				.childMinimum(format(ErrorCode.NUMERATOR_DENOMINATOR_INCORRECT_CHILD), 1, TemplateId.ACI_AGGREGATE_COUNT)
				.childMaximum(format(ErrorCode.NUMERATOR_DENOMINATOR_TOO_MANY_CHILDREN), 1, TemplateId.ACI_AGGREGATE_COUNT);
		if (getDetails().isEmpty()) {
			validateAggregateCount(
					node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT));
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
		check(aggregateCountNode)
				.singleValue(format(ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE), AGGREGATE_COUNT_FIELD)
				.intValue(format(ErrorCode.NUMERATOR_DENOMINATOR_MUST_BE_INTEGER), AGGREGATE_COUNT_FIELD)
				.greaterThan(format(ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE), -1);
	}

	private LocalizedError format(ErrorCode error) {
		return error.format(nodeName);
	}
}
