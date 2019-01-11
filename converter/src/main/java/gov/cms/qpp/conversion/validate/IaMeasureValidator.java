package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates IaMeasure Node - expects a child MEASURE_PERFORMED with a  Y or N value
 */
@Validator(TemplateId.IA_MEASURE)
public class IaMeasureValidator extends NodeValidator {

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
	protected void performValidation(Node node) {
		checkErrors(node)
				.childExact(ErrorCode.IA_MEASURE_INCORRECT_CHILDREN_COUNT, 1, TemplateId.MEASURE_PERFORMED);
	}
}