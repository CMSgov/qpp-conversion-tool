package gov.cms.qpp.conversion.validate;

import java.util.List;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates Aggregate Count
 */
@Validator(value = TemplateId.ACI_AGGREGATE_COUNT, required = true)
public class AggregateCountValidator extends NodeValidator {

	public static final String VALUE_ERROR = "Aggregate count value is required.";
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
			.value(VALUE_ERROR, "aggregateCount")
			.intValue(TYPE_ERROR, "aggregateCount");
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// No current cross node Aggregate Count validations
	}
}
