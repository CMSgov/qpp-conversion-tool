package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * Validates Measure Data - an Aggregate Count child
 */
@Validator(templateId = TemplateId.MEASURE_DATA_CMS_V2, required = true)
public class MeasureDataValidator extends NodeValidator {

	public static final String TYPE_ERROR = "Measure data value is required.";
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
		if (node == null) {
			getValidationErrors().add(new ValidationError(TYPE_ERROR));
			return;
		}

		Checker checker = check(node)
				.hasChildren(MISSING_AGGREGATE_COUNT)
				.childMinimum(MISSING_AGGREGATE_COUNT, 1, TemplateId.ACI_AGGREGATE_COUNT)
				.childMaximum(MISSING_AGGREGATE_COUNT, 1, TemplateId.ACI_AGGREGATE_COUNT);

		if (getValidationErrors().isEmpty()) {
			Node child = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
			checker.checkChild(child, true)
					.value(AggregateCountValidator.VALUE_ERROR, "aggregateCount")
					.intValue(AggregateCountValidator.TYPE_ERROR, "aggregateCount")
					.greaterThan(INVALID_VALUE, 0)
					.markValidated();
		}
	}

	/**
	 * Checks the interdependancy of nodes in the parsed tree.
	 * IA Measure Performed has no dependencies on other nodes in the document.
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// No current cross node Aggregate Count validations
	}
}