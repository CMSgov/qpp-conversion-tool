package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import java.util.List;

/**
 * Validates a Quality Measure Section node.
 */
@Validator(value = TemplateId.MEASURE_SECTION_V2, required = true)
public class QualityMeasureSectionValidator extends NodeValidator {
	protected static final String MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR
			= "The Quality Measure Section must have a only one Reporting Parameter ACT";

	/**
	 * Validate that the Quality Measure Section contains...
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.childMinimum(MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR, 1,
					TemplateId.REPORTING_PARAMETERS_ACT)
			.childMaximum(MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR, 1,
					TemplateId.REPORTING_PARAMETERS_ACT);
	}

	/**
	 * Does nothing.
	 *
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		//no cross-node validations required
	}
}
