package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Test class for ReportingParametersActEncoder
 */
class ReportingParametersActEncoderTest {

	@Test
	void internalEncode() throws Exception {
		Node reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersActNode.putValue(ReportingParametersActEncoder.PERFORMANCE_START,"20170101");
		reportingParametersActNode.putValue(ReportingParametersActEncoder.PERFORMANCE_END,"20171231");
		JsonWrapper outputWrapper = new JsonWrapper();
		ReportingParametersActEncoder encoder = new ReportingParametersActEncoder(new Context());
		encoder.internalEncode(outputWrapper, reportingParametersActNode);
		String performanceStart = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_START);
		String performanceEnd = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_END);

		assertThat(performanceStart).isEqualTo("2017-01-01");
		assertThat(performanceEnd).isEqualTo("2017-12-31");
	}

	@Test
	void missingValuesTest() throws Exception {
		Node reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);

		JsonWrapper outputWrapper = new JsonWrapper();
		ReportingParametersActEncoder encoder = new ReportingParametersActEncoder(new Context());
		encoder.internalEncode(outputWrapper, reportingParametersActNode);
		String performanceStart = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_START);
		String performanceEnd = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_END);

		assertThat(performanceStart).isNull();
		assertThat(performanceEnd).isNull();
	}
}