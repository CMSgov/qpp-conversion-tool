package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.util.DuplicationCheckHelper;

import static gov.cms.qpp.conversion.model.Constants.AGGREGATE_COUNT;

/**
 * Validates Aggregate Count
 */
@Validator(TemplateId.PI_AGGREGATE_COUNT)
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
	protected void performValidation(Node node) {
		checkErrors(node)
			.singleValue(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR
				.format(node.getParent().getType().name(),
					DuplicationCheckHelper.calculateDuplications(node, AGGREGATE_COUNT)),
				AGGREGATE_COUNT)
			.intValue(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER, AGGREGATE_COUNT);

	}
}
