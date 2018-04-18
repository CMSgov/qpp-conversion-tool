package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class CpcPerformancePeriodValidationTest {

	private CpcPerformancePeriodValidation cpcValidator;
	private Node node;

	@BeforeEach
	void setup() {
		cpcValidator = new CpcPerformancePeriodValidation();
		node = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_YEAR, "2017");
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "20170101");
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
	}

	@Test
	void testPerformancePeriodIsValid() {
		cpcValidator.internalValidateSingleNode(node);
		assertWithMessage("Should be no errors")
				.that(cpcValidator.getDetails()).isEmpty();
	}

	@Test
	void testPerformancePeriodStartIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "not what we want");
		cpcValidator.internalValidateSingleNode(node);

		assertWithMessage("Should result in a performance start error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_PERFORMANCE_PERIOD_START_JAN12017);
	}

	@Test
	void testPerformancePeriodEndIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "not what we want");
		cpcValidator.internalValidateSingleNode(node);
		assertWithMessage("Should result in a performance end error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_PERFORMANCE_PERIOD_END_DEC312017);
	}
}