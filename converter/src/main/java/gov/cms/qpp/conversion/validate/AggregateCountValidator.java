package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates Aggregate Count
 */
@Validator(TemplateId.ACI_AGGREGATE_COUNT)
public class AggregateCountValidator extends NodeValidator {

	/**
	 * Validates a single Aggregate Count {@link gov.cms.qpp.conversion.model.Node}.
	 * <p>
	 * Validates the following.
	 * <ul>
	 * <li>An integer value named "aggregateCount" was decoded from the source element</li>
	 * </ul>
	 *
	 * @param node Node that represents a Aggregate Count.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.singleValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR, "aggregateCount")
			.intValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER, "aggregateCount");
	}
}
