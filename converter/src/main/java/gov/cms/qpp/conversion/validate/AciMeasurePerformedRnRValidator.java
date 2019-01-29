package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * This Validator checks that the Measure ID is present.
 */
@Validator(TemplateId.PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
public class AciMeasurePerformedRnRValidator extends NodeValidator {

	/**
	 * internalValidateSingleNode Checks that this node has a child and that the node contains a valid measureId
	 *
	 * @param node Node parsed xml fragment under consideration
	 */
	@Override
	protected void performValidation(Node node) {
		forceCheckErrors(node)
			.childExact(ErrorCode.PI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_EXACT, 1, TemplateId.MEASURE_PERFORMED)
			.singleValue(ErrorCode.PI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR, "measureId");
	}
}