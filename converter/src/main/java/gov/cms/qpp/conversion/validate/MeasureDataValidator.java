package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.util.DuplicationCheckHelper;


/**
 * Validates Measure Data - an Aggregate Count child
 */
@Validator(TemplateId.MEASURE_DATA_CMS_V4)
public class MeasureDataValidator extends NodeValidator {
	protected static final String EMPTY_POPULATION_ID = "empty population id";

	/**
	 * Validates a single Measure Data Value {@link Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *    <li>An integer value with a name in the list from MeasureDataDecoder.MEASURES</li>
	 *    <li>The string value is an integer</li>
	 * </ul>
	 *
	 * @param node Node that represents a IA Measure Performed.
	 */
	@Override
	protected void performValidation(Node node) {
		String populationId = node.getValue(MeasureDataDecoder.MEASURE_POPULATION);
		if (populationId == null) {
			populationId = EMPTY_POPULATION_ID;
		}

		Checker checker = checkErrors(node)
				.hasChildren(ProblemCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT.format(populationId))
				.childExact(ProblemCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT.format(populationId),
					1, TemplateId.PI_AGGREGATE_COUNT);

		if (!checker.shouldShortcut()) {
			Node child = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);
			checkErrors(child)
					.singleValue(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR.format(node.getType().name(),
						DuplicationCheckHelper.calculateDuplications(child, AggregateCountDecoder.AGGREGATE_COUNT)),
						AggregateCountDecoder.AGGREGATE_COUNT)
					.intValue(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER, AggregateCountDecoder.AGGREGATE_COUNT)
					.greaterThan(ProblemCode.MEASURE_DATA_VALUE_NOT_INTEGER.format(populationId), -1);
		}
	}
}