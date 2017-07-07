package gov.cms.qpp.conversion.validate;


import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

@Validator(value = TemplateId.REPORTING_PARAMETERS_ACT, required = true)
public class ReportingParametersActValidator extends NodeValidator {
	public static final String SINGLE_PERFORMANCE_START = "Must have one and only one performance start";
	private static final String SINGLE_PERFORMANCE_END = "Must have one and only one performance end";

	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
				.singleValue(SINGLE_PERFORMANCE_START, ReportingParametersActDecoder.PERFORMANCE_START)
				.singleValue(SINGLE_PERFORMANCE_END, ReportingParametersActDecoder.PERFORMANCE_END);
	}
}
