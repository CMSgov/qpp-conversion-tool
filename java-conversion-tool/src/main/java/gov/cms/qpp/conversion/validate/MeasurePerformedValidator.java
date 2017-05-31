package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ValidationError;

import java.util.List;

/**
 * Validate The Measure Performed Node 2.16.840.1.113883.10.20.27.3.27
 */
@Validator(value = TemplateId.MEASURE_PERFORMED, required = true)
public class MeasurePerformedValidator extends NodeValidator {
	private static final String FIELD = "measurePerformed";

	/**
	 * An string value named "measurePerformed" was decoded from the source element<
	 * The string value is either a Y or an N
	 *
	 * @param node Node that represents a Measure Performed.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		String value = node.getValue(FIELD);
		if (!("Y".equals(value) || "N".equals(value))) {
			addValidationError(new ValidationError(IaMeasureValidator.TYPE_ERROR, node.getPath()));
		}
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
