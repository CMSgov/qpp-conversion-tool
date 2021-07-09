package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V4, program = Program.PCF)
public class PcfQualityMeasureIdValidator extends CpcQualityMeasureIdValidator {

	@Override
	protected void performValidation(final Node node) {
		super.performValidation(node);
	}
}
