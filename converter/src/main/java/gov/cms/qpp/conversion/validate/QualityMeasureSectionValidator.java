package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;

import static gov.cms.qpp.conversion.model.Constants.CATEGORY_SECTION_V5;
import static gov.cms.qpp.conversion.model.Constants.MEASURE_ID;

/**
 * Validates a Quality Measure Section node.
 */
@Validator(TemplateId.MEASURE_SECTION_V5)
public class QualityMeasureSectionValidator extends NodeValidator {

	/**
	 * Validate that the Quality Measure Section contains...
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		checkErrors(node)
			.childExact(ProblemCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT, 1,
				TemplateId.REPORTING_PARAMETERS_ACT)
			.childMinimum(ProblemCode.MEASURE_SECTION_MISSING_MEASURE, 1, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V5)
		    .oneChildPolicy(ProblemCode.MEASURES_RNR_WITH_DUPLICATED_MEASURE_GUID, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V5,
					childNode -> childNode.getValue(MEASURE_ID))
			.singleValue(ProblemCode.MEASURE_SECTION_V5_REQUIRES_CATEGORY_SECTION, CATEGORY_SECTION_V5)
			.valueIs(ProblemCode.MEASURE_SECTION_V5_REQUIRES_CATEGORY_SECTION, CATEGORY_SECTION_V5,
				TemplateId.CATEGORY_REPORT_V5.getExtension());

	}
}
