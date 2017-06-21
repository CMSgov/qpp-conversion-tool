package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates Measure Data - an Aggregate Count child
 */
@Validator(value = TemplateId.MEASURE_DATA_CMS_V2, required = true)
public class MeasureDataValidator extends NodeValidator {

	public static final String MISSING_AGGREGATE_COUNT  = "Measure performed must have exactly one Aggregate Count.";
	public static final String INVALID_VALUE = "Measure data must be a positive integer value";

	/**
	 * Validates a single Measure Data Value {@link Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *    <li>An integer value with a name in the list from MeasureDataDecoder.MEASURES</li>
	 *    <li>The string value is an integer/li>
	 *</ul>
	 *
	 * @param node Node that represents a IA Measure Performed.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
				.hasChildren(MISSING_AGGREGATE_COUNT)
				.childMinimum(MISSING_AGGREGATE_COUNT, 1, TemplateId.ACI_AGGREGATE_COUNT)
				.childMaximum(MISSING_AGGREGATE_COUNT, 1, TemplateId.ACI_AGGREGATE_COUNT);

		if (getDetails().isEmpty()) {
			Node child = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);
			check(child)
					.value(AggregateCountValidator.VALUE_ERROR, "aggregateCount")
					.intValue(AggregateCountValidator.TYPE_ERROR, "aggregateCount")
					.greaterThan(INVALID_VALUE, -1);
		}
	}
}