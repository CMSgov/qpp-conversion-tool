package gov.cms.qpp.conversion.encode.helper;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the ReportingParameters Helper class
 * Reporting Parameters are children of Clinical Document.
 * They are used by Clinical Document Encoder and
 * QualitySectionEncoder
 */
public class ReportingParametersTest {
	/**
	 * Test the needed parameters are available in the map
	 */
	@Test
	public void getReportingNodeBothValid() {

		Map<String, Node> childMapByTemplateId = new HashMap();
		Node reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId());
		Node reportingActNode = new Node(reportingParameterNode, TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId());
		reportingParameterNode.addChildNode(reportingActNode);
		reportingActNode.putValue("performanceStart", "2017-01-01");
		reportingActNode.putValue("performanceEnd", "2017-12-31");
		childMapByTemplateId.put(
				TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId(), reportingParameterNode);

		Optional<Node> result = ReportingParameters.getReportingNode(childMapByTemplateId);

		Optional<String> performanceStart = result.flatMap(p -> Optional.of(p.getValue("performanceStart")));
		Optional<String> performanceEnd = result.flatMap(p -> Optional.of(p.getValue("performanceEnd")));

		assertThat("Expecting a performance start value ", performanceStart.isPresent(), is(true));
		assertThat("Expecting  performance start 2017-01-01 ", performanceStart.get(), is("2017-01-01"));

		assertThat("Expecting a performance end value ", performanceEnd.isPresent(), is(true));
		assertThat("Expecting  performance end 2017-12-31 ", performanceEnd.get(), is("2017-12-31"));

	}


	/**
	 * Test that the parameter is optional
	 */
	@Test
	public void getReportingNodeEndMissing() {

		Map<String, Node> childMapByTemplateId = new HashMap();
		Node reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId());
		Node reportingActNode = new Node(reportingParameterNode, TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId());
		reportingParameterNode.addChildNode(reportingActNode);
		reportingActNode.putValue("performanceStart", "2017-01-01");
		reportingActNode.putValue("performanceEnd", "");
		childMapByTemplateId.put(
				TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId(), reportingParameterNode);

		Optional<Node> result = ReportingParameters.getReportingNode(childMapByTemplateId);

		Optional<String> performanceStart = result.flatMap(p -> Optional.of(p.getValue("performanceStart")));
		Optional<String> performanceEnd = result.flatMap(p -> Optional.of(p.getValue("performanceEnd")));

		assertThat("Expecting a performance start value ", performanceStart.isPresent(), is(true));
		assertThat("Expecting  performance start 2017-01-01 ", performanceStart.get(), is("2017-01-01"));

		assertThat("Expecting no performance end value ", performanceEnd.isPresent(), is(true));
		assertThat("Expecting performance end to be empty ", performanceEnd.get(), is(""));

	}
}