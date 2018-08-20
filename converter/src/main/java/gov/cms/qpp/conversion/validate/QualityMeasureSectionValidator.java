package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.QualityMeasureIdDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Error;
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
			.childMinimum(ErrorCode.QUALITY_MEASURE_SECTION_MISSING_MEASURE_RNR,
				1, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
		    .oneChildPolicy(ErrorCode.MEASURES_RNR_WITH_DUPLICATED_MEASURE_GUID, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2,
					childNode -> childNode.getValue(QualityMeasureIdDecoder.MEASURE_ID))
			.noParentChildDuplications(ErrorCode.QUALITY_MEASURE_SECTION_AND_RNR_DUPLICATE_REPORTING_PARAM_REQUIREMENT,
				TemplateId.REPORTING_PARAMETERS_ACT, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);

		if (node.getChildNodes(TemplateId.REPORTING_PARAMETERS_ACT).count() > 0) {
			check(node).childExact(
				ErrorCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT,
				1, TemplateId.REPORTING_PARAMETERS_ACT);
		} else {
			node.getChildNodes(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2).forEach(qualityMeasureNode ->
				check(qualityMeasureNode).incompleteValidation().childExact(
					ErrorCode.QUALITY_MEASURE_SECTION_RNR_REQUIRED_REPORTING_PARAM_REQUIREMENT,
					1, TemplateId.REPORTING_PARAMETERS_ACT));
		}
	}
}
