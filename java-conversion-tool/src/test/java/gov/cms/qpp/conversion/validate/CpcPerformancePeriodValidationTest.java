package gov.cms.qpp.conversion.validate;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class CpcPerformancePeriodValidationTest {

	private CpcPerformancePeriodValidation cpcValidator;
	private Node node;

	@Before
	public void setup() {
		cpcValidator = new CpcPerformancePeriodValidation();
		node = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_YEAR, "2017");
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "20170101");
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
	}

	@Test
	public void testPerformancePeriodIsValid() {
		cpcValidator.internalValidateSingleNode(node);
		assertThat(cpcValidator.getDetails(), empty());
	}

	@Test
	public void testPerformancePeriodStartIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "not what we want");
		cpcValidator.internalValidateSingleNode(node);
		assertThat(cpcValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcPerformancePeriodValidation.PERFORMANCE_START_JAN12017));
	}

	@Test
	public void testPerformancePeriodEndIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "not what we want");
		cpcValidator.internalValidateSingleNode(node);
		assertThat(cpcValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcPerformancePeriodValidation.PERFORMANCE_END_DEC312017));
	}
}