package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates a Quality Measure Section node.
 */
@Validator(TemplateId.MEASURE_SECTION_V2)
public class QualityMeasureSectionValidator extends NodeValidator {

	/**
	 * Validate that the Quality Measure Section contains...
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.childMinimum(ErrorCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT_ERROR, 1,
					TemplateId.REPORTING_PARAMETERS_ACT)
			.childMaximum(ErrorCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT_ERROR, 1,
					TemplateId.REPORTING_PARAMETERS_ACT);
	}
}
