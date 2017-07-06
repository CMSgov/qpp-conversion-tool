package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * This Validator checks that the Measure ID is present.
 */
@Validator(value = TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS, required = true)
public class AciMeasurePerformedRnRValidator extends NodeValidator {

	private static final String MEASURE_ID_IS_REQUIRED =
			"An ACI Measure Performed RnR's requires a single Measure ID";
	private static final String MEASURE_PERFORMED_IS_REQUIRED =
			"The ACI Measure Performed RnR's Measure Performed is required";
	private static final String MEASURE_PERFORMED_CAN_ONLY_BE_PRESENT_ONCE =
			"The ACI Measure Performed RnR's Measure Performed can only be present once";

	/**
	 * internalValidateSingleNode Checks that this node has a child and that the node contains a valid measureId
	 *
	 * @param node Node parsed xml fragment under consideration
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		thoroughlyCheck(node)
			.hasChildren(MEASURE_PERFORMED_IS_REQUIRED)
			.childMinimum(MEASURE_PERFORMED_IS_REQUIRED, 1, TemplateId.MEASURE_PERFORMED)
			.childMaximum(MEASURE_PERFORMED_CAN_ONLY_BE_PRESENT_ONCE, 1, TemplateId.MEASURE_PERFORMED)
			.singleValue(MEASURE_ID_IS_REQUIRED, "measureId");
	}
}