package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates Measure Data - an Aggregate Count child
 */
@Validator(TemplateId.MEASURE_DATA_CMS_V2)
public class MeasureDataValidator extends NodeValidator {

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
				.hasChildren(ErrorCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT)
				.childMinimum(ErrorCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT, 1, TemplateId.ACI_AGGREGATE_COUNT)
				.childMaximum(ErrorCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT, 1, TemplateId.ACI_AGGREGATE_COUNT);

		if (getDetails().isEmpty()) {
			Node child = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);
			check(child)
					.singleValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR, "aggregateCount")
					.intValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER, "aggregateCount")
					.greaterThan(ErrorCode.MEASURE_DATA_VALUE_NOT_INTEGER, -1);
		}
	}
}