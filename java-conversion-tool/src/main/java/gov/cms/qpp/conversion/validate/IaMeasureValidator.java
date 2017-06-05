package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * Validates IaMeasure Node - expects a child MEASURE_PERFORMED with a  Y or N value
 */
@Validator(value = TemplateId.IA_MEASURE, required = true)
public class IaMeasureValidator extends NodeValidator {

	public static final String TYPE_ERROR = "Measure performed value is required and must be either a Y or an N.";
	public static final String INCORRECT_CHILDREN_COUNT = "Measure performed must have exactly one child.";

	/**
	 * Validates a single IA Measure Performed Value {@link Node}.
	 * <p>
	 * Validates the following.
	 * <ul>
	 * <li>An string value named "measurePerformed" was decoded from the source element</li>
	 * <li>The string value is either a Y or an N</li>
	 * </ul>
	 *
	 * @param node Node that represents a IA Measure Performed.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		Checker.check(node, getValidationErrors())
				.childMinimum(INCORRECT_CHILDREN_COUNT, 1, TemplateId.MEASURE_PERFORMED)
				.childMaximum(INCORRECT_CHILDREN_COUNT, 1, TemplateId.MEASURE_PERFORMED);
	}

	/**
	 * Checks the interdependency of nodes in the parsed tree.
	 * IA Measure Performed has no dependencies on other nodes in the document.
	 *
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// No current cross node Aggregate Count validations
	}
}