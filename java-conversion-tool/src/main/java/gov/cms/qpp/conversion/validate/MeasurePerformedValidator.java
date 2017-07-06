package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validate The Measure Performed Node 2.16.840.1.113883.10.20.27.3.27
 */
@Validator(value = TemplateId.MEASURE_PERFORMED, required = true)
public class MeasurePerformedValidator extends NodeValidator {
	private static final String FIELD = "measurePerformed";
	private static final String[] BOOLEAN_VALUES = {"Y", "N"};

	/**
	 * An string value named "measurePerformed" was decoded from the source element<
	 * The string value is either a Y or an N
	 *
	 * @param node Node that represents a Measure Performed.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.singleValue(IaMeasureValidator.TYPE_ERROR, FIELD)
			.valueIn(IaMeasureValidator.TYPE_ERROR, FIELD, BOOLEAN_VALUES);
	}
}
