package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates Aggregate Count
 */
@Validator(TemplateId.ACI_AGGREGATE_COUNT)
public class AggregateCountValidator extends NodeValidator {

	public static final String VALUE_ERROR = "A single aggregate count value is required.";
	public static final String TYPE_ERROR = "Aggregate count value must be an integer.";

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
			.singleValue(VALUE_ERROR, "aggregateCount")
			.intValue(TYPE_ERROR, "aggregateCount");
	}
}
