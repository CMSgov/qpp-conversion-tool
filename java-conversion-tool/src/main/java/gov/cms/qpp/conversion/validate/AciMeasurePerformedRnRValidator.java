package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Validator checks that the Measure ID is present.
 */
@Validator(templateId = TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
public class AciMeasurePerformedRnRValidator extends NodeValidator {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(AciMeasurePerformedRnRValidator.class);
	private static final String MEASURE_ID_IS_REQUIRED = "The ACI Measure Performed RnR's Measure ID is required";
	private static final String MEASURE_PERFORMED_IS_REQUIRED = "The ACI Measure Performed RnR's Measure Performed is required";

	/**
	 * internalValidateSingleNode Checks that this node has a child and that the node contains a valid measureId
	 *
	 * @param node Node parsed xml fragment under consideration
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		String measureId = node.getValue("measureId");
		if (measureId == null || measureId.isEmpty()) {
			this.addValidationError(new ValidationError(MEASURE_ID_IS_REQUIRED, node.getPath()));
			DEV_LOG.error(MEASURE_ID_IS_REQUIRED);
		}

		Node measurePerformed = node.findFirstNode("value");
		if (measurePerformed == null) {
			this.addValidationError(new ValidationError(MEASURE_PERFORMED_IS_REQUIRED, node.getPath()));
			DEV_LOG.error(MEASURE_PERFORMED_IS_REQUIRED);
		}
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// nothing
	}
}