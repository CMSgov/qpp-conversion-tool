package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.QualityMeasureIdDecoder;
import gov.cms.qpp.conversion.decode.QualitySectionDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates a Quality Measure Section node.
 */
@Validator(TemplateId.MEASURE_SECTION_V3)
public class QualityMeasureSectionValidator extends NodeValidator {

	/**
	 * Validate that the Quality Measure Section contains...
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		checkErrors(node)
			.childExact(ErrorCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT, 1,
				TemplateId.REPORTING_PARAMETERS_ACT)
			.childMinimum(ErrorCode.MEASURE_SECTION_MISSING_MEASURE, 1, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
		    .oneChildPolicy(ErrorCode.MEASURES_RNR_WITH_DUPLICATED_MEASURE_GUID, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2,
					childNode -> childNode.getValue(QualityMeasureIdDecoder.MEASURE_ID))
			.singleValue(ErrorCode.MEASURE_SECTION_V4_REQUIRED, QualitySectionDecoder.MEASURE_SECTION_V4)
			.valueIs(ErrorCode.MEASURE_SECTION_V4_REQUIRED, QualitySectionDecoder.MEASURE_SECTION_V4,
				TemplateId.MEASURE_SECTION_V4.getExtension());

	}
}
