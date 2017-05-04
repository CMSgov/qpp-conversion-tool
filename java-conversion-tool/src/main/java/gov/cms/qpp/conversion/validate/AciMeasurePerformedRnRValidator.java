package gov.cms.qpp.conversion.validate;

import java.util.List;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * This Validator checks that the Measure ID is present.
 */
@Validator(templateId = TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
public class AciMeasurePerformedRnRValidator extends NodeValidator {

	private static final String MEASURE_ID_IS_REQUIRED =
			"The ACI Measure Performed RnR's Measure ID is required";
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
			.value(MEASURE_ID_IS_REQUIRED, "measureId");
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// nothing
	}
}