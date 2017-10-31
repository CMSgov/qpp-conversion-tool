package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

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
		assertWithMessage("Should be no errors")
				.that(cpcValidator.getDetails()).isEmpty();
	}

	@Test
	public void testPerformancePeriodStartIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "not what we want");
		cpcValidator.internalValidateSingleNode(node);

		assertWithMessage("Should result in a performance start error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(CpcPerformancePeriodValidation.PERFORMANCE_START_JAN12017);
	}

	@Test
	public void testPerformancePeriodEndIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "not what we want");
		cpcValidator.internalValidateSingleNode(node);
		assertWithMessage("Should result in a performance end error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(CpcPerformancePeriodValidation.PERFORMANCE_END_DEC312017);
	}
}