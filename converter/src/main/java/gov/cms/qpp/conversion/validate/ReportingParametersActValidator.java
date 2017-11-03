package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

@Validator(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActValidator extends NodeValidator {

	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
				.singleValue(ErrorCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START, 
						ReportingParametersActDecoder.PERFORMANCE_START)
				.singleValue(ErrorCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END, 
						ReportingParametersActDecoder.PERFORMANCE_END)
				.value(ErrorCode.REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR, 
						ReportingParametersActDecoder.PERFORMANCE_YEAR);
	}
}
