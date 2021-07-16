package gov.cms.qpp.conversion.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class PcfPerformancePeriodValidationTest {

	private PcfPerformancePeriodValidation validator;
	private Node node;

	@BeforeEach
	void setup() {
		validator = new PcfPerformancePeriodValidation();
		node = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_YEAR, "2021");
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "20210101");
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20211231");
	}

	@Test
	void testPerformancePeriodIsValid() {
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertWithMessage("Should be no errors")
			.that(details).isEmpty();
	}

	@Test
	void testPerformancePeriodStartIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "not what we want");
		List<Detail> details = validator.validateSingleNode(node).getErrors();

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_PERFORMANCE_PERIOD_START);
	}

	@Test
	void testPerformancePeriodEndIsInvalid() {
		node.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "not what we want");
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_PERFORMANCE_PERIOD_END);
	}
}
