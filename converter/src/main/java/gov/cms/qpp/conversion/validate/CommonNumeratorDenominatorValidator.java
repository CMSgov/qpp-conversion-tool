package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;

import static gov.cms.qpp.conversion.model.Constants.AGGREGATE_COUNT;

/**
 * Super class for AciNumeratorValidator and AciDenominatorValidator classes
 * Factored out common functionality
 */
public class CommonNumeratorDenominatorValidator extends NodeValidator {

	/**
	 * The node type name as in "Numerator" or "Denominator"
	 */
	protected String nodeName;

	/**
	 * internalValidateSingleNode inspects the node for certain validations.
	 * Will add validation errors if any exist
	 *
	 * @param node Node parsed xml fragment under consideration
	 */
	@Override
	protected void performValidation(Node node) {
		Checker checker = checkErrors(node).childExact(format(ProblemCode.NUMERATOR_DENOMINATOR_CHILD_EXACT), 1, TemplateId.PI_AGGREGATE_COUNT);
		if (!checker.shouldShortcut()) {
			validateAggregateCount(
					node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT));
		}
	}

	/**
	 * Common ACI numerator denominator validation for aggregate count. Marks the
	 * {@link TemplateId#PI_AGGREGATE_COUNT} node as validated to prevent duplicate
	 * validation by the {@link AggregateCountValidator}
	 *
	 * @param aggregateCountNode aggregate count node
	 */
	private void validateAggregateCount(Node aggregateCountNode) {
		String aggregateCountValue = aggregateCountNode.getValue(AGGREGATE_COUNT);
		if (aggregateCountValue == null) {
			aggregateCountValue = "empty";
		}
		checkErrors(aggregateCountNode)
				.singleValue(format(ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE, aggregateCountValue),
					AGGREGATE_COUNT)
				.intValue(format(ProblemCode.NUMERATOR_DENOMINATOR_MUST_BE_INTEGER, aggregateCountValue),
					AGGREGATE_COUNT)
				.greaterThan(format(ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE, aggregateCountValue), -1);
	}

	private LocalizedProblem format(ProblemCode error) {
		return error.format(nodeName, nodeName);
	}

	private LocalizedProblem format(ProblemCode error, String value) {
		return error.format(nodeName, value);
	}
}
