package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * Test class for ReportingParametersActEncoder
 */
public class ReportingParametersActEncoderTest {
	@Test
	public void internalEncode() throws Exception {
		Node reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersActNode.putValue(ReportingParametersActEncoder.PERFORMANCE_START,"20170101");
		reportingParametersActNode.putValue(ReportingParametersActEncoder.PERFORMANCE_END,"20171231");
		JsonWrapper outputWrapper = new JsonWrapper();
		ReportingParametersActEncoder encoder = new ReportingParametersActEncoder(new Context());
		encoder.internalEncode(outputWrapper, reportingParametersActNode);
		String performanceStart = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_START);
		String performanceEnd = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_END);

		assertWithMessage("Performance Start = 2017-01-01")
				.that(performanceStart).isEqualTo("2017-01-01");
		assertWithMessage("Performance End = 2017-12-31")
				.that(performanceEnd).isEqualTo("2017-12-31");
	}

	@Test
	public void missingValuesTest() throws Exception {
		Node reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);

		JsonWrapper outputWrapper = new JsonWrapper();
		ReportingParametersActEncoder encoder = new ReportingParametersActEncoder(new Context());
		encoder.internalEncode(outputWrapper, reportingParametersActNode);
		String performanceStart = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_START);
		String performanceEnd = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_END);

		assertWithMessage("Performance Start is null")
				.that(performanceStart).isNull();
		assertWithMessage("Performance End = is null")
				.that(performanceEnd).isNull();
	}
}