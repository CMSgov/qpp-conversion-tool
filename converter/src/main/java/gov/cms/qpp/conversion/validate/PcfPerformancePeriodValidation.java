package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

@Validator(value = TemplateId.REPORTING_PARAMETERS_ACT, program = Program.PCF)
public class PcfPerformancePeriodValidation extends CpcPerformancePeriodValidation {

	@Override
	protected void performValidation(final Node node) {
		super.performValidation(node);
	}
}
