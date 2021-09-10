package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

@Validator(value = TemplateId.MEASURE_DATA_CMS_V4, program = Program.PCF)
public class PcfMeasureDataValidator extends CpcMeasureDataValidator {

	@Override
	protected void performValidation(final Node node) {
		super.performValidation(node);
	}
}
