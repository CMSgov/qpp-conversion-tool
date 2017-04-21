package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * Validates IA Measure Performed Value - expects a  Y or N
 */
@Validator(templateId = TemplateId.IA_MEASURE, required = true)
public class IaMeasurePerformedValidator extends NodeValidator {

	public static final String TYPE_ERROR = "Measure performed value is required and must be either a Y or an N.\n\t%s";
	public static final String INCORRECT_CHILDREN_COUNT  = "Measure performed must have exactly one child.\n\t%s";
	private static final String FIELD = "measurePerformed";

	/**
	 * Validates a single IA Measure Performed Value {@link Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *    <li>An string value named "measurePerformed" was decoded from the source element</li>
	 *    <li>The string value is either a Y or an N</li>
	 *</ul>
	 *
	 * @param node Node that represents a IA Measure Performed.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		List<Node> children = node.getChildNodes();
		if (children == null || children.size() != 1) {
			addValidationError(new ValidationError(String.format(INCORRECT_CHILDREN_COUNT, node.toString()), node.getPath()));
			return;
		}
		String value = children.get(0).getValue(FIELD);
		if (!("Y".equals(value) || "N".equals(value))) {
			addValidationError(new ValidationError(String.format(TYPE_ERROR, node.toString()), children.get(0).getPath()));
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