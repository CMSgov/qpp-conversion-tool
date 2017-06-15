package gov.cms.qpp.conversion.encode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

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
		ReportingParametersActEncoder encoder = new ReportingParametersActEncoder();
		encoder.internalEncode(outputWrapper, reportingParametersActNode);
		String performanceStart = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_START);
		String performanceEnd = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_END);

		assertThat("Performance Start = 2017-01-01", performanceStart, is("2017-01-01"));
		assertThat("Performance End = 2017-12-31", performanceEnd, is("2017-12-31"));
	}

	@Test
	public void missingValuesTest() throws Exception {
		Node reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);

		JsonWrapper outputWrapper = new JsonWrapper();
		ReportingParametersActEncoder encoder = new ReportingParametersActEncoder();
		encoder.internalEncode(outputWrapper, reportingParametersActNode);
		String performanceStart = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_START);
		String performanceEnd = outputWrapper.getString(ReportingParametersActEncoder.PERFORMANCE_END);

		assertThat("Performance Start is null", performanceStart, is(nullValue()));
		assertThat("Performance End = is null", performanceEnd, is(nullValue()));
	}
}