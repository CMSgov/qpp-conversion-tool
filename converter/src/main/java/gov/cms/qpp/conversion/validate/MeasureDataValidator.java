package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.util.DuplicationCheckHelper;


/**
 * Validates Measure Data - an Aggregate Count child
 */
@Validator(TemplateId.MEASURE_DATA_CMS_V2)
public class MeasureDataValidator extends NodeValidator {
	protected static final String EMPTY_POPULATION_ID = "empty population id";

	/**
	 * Validates a single Measure Data Value {@link Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *    <li>An integer value with a name in the list from MeasureDataDecoder.MEASURES</li>
	 *    <li>The string value is an integer</li>
	 *</ul>
	 *
	 * @param node Node that represents a IA Measure Performed.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		String populationId = node.getValue(MeasureDataDecoder.MEASURE_POPULATION);
		if (populationId == null) {
			populationId = EMPTY_POPULATION_ID;
		}

		check(node)
				.hasChildren(ErrorCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT.format(populationId))
				.childExact(ErrorCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT.format(populationId),
					1, TemplateId.ACI_AGGREGATE_COUNT);

		if (getDetails().isEmpty()) {
			Node child = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);
			check(child)
					.singleValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR.format(node.getType().name(),
						DuplicationCheckHelper.calculateDuplications(child, AggregateCountDecoder.AGGREGATE_COUNT)),
						AggregateCountDecoder.AGGREGATE_COUNT)
					.intValue(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER, AggregateCountDecoder.AGGREGATE_COUNT)
					.greaterThan(ErrorCode.MEASURE_DATA_VALUE_NOT_INTEGER.format(populationId), -1);
		}
	}
}