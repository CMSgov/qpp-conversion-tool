package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.util.DuplicationCheckHelper;

import java.util.Collection;
import java.util.List;

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
			.singleValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR
				.format(node.getParent().getType().name(), DuplicationCheckHelper.calculateDuplications(node)),
				AggregateCountDecoder.AGGREGATE_COUNT)
			.intValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER, AggregateCountDecoder.AGGREGATE_COUNT);

	}
}
