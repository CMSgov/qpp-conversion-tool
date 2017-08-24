package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class IaSectionEncoderTest {

	private static final String EXPECTED = "{\n  \"category\" : \"ia\",\n  \"submissionMethod\" : \"electronicHealthRecord\",\n  \"measurements\" : [ "
			+ "{\n    \"measureId\" : \"IA_EPA_1\",\n    \"value\" : true\n  } ],\n  \"performanceStart\" : \"2017-01-01\",\n  \"performanceEnd\" : \"2017-12-31\"\n}";

	private static final String EXPECTED_NO_MEASURE = "{\n  \"category\" : \"ia\",\n  \"submissionMethod\" : \"electronicHealthRecord\",\n  \"performanceStart\" : \"2017-01-01\",\n  \"performanceEnd\" : \"2017-12-31\"\n}";

	private static final String EXPECTED_NO_MEASURE_VALUE_1 = "{\n  \"category\" : \"ia\",\n  "
			+ "\"submissionMethod\" : \"electronicHealthRecord\",\n  \"measurements\" : [ "
			+ "{\n    \"measureId\" : \"IA_EPA_1\"\n  } ],\n  \"performanceStart\" : \"2017-01-01\",\n  \"performanceEnd\" : \"2017-12-31\"\n}";

	private Node iaSectionNode;
	private Node iaMeasureNode;
	private Node iaMeasurePerformedNode;
	private Node iaReportingSectionNode;
	private List<Node> nodes;

	@Before
	public void createNode() {
		iaMeasurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		iaMeasurePerformedNode.putValue("measurePerformed", "Y");

		iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		iaMeasureNode.putValue("measureId", "IA_EPA_1");
		iaMeasureNode.addChildNode(iaMeasurePerformedNode);

		iaSectionNode = new Node(TemplateId.IA_SECTION);
		iaSectionNode.putValue("category", "ia");
		iaSectionNode.addChildNode(iaMeasureNode);

		iaReportingSectionNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		iaReportingSectionNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "20170101");
		iaReportingSectionNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
		iaSectionNode.addChildNode(iaReportingSectionNode);

		nodes = new ArrayList<>();
		nodes.add(iaSectionNode);
	}

	@Test
	public void testEncoder() {
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}

		assertThat("expected encoder to return a json representation of an IA Section node", sw.toString(),
				is(EXPECTED));
	}

	@Test
	public void testEncoderWithoutMeasure() {
		iaSectionNode.getChildNodes().remove(iaMeasureNode);
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}

		assertThat("expected encoder to return a json representation of an IA Section node", sw.toString(),
				is(EXPECTED_NO_MEASURE));
	}
	
	@Test
	public void testEncoderWithoutMeasureValue1() {
		iaMeasureNode.getChildNodes().remove(iaMeasurePerformedNode);
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}

		assertThat("expected encoder to return a json representation of an IA Section node", sw.toString(),
				is(EXPECTED_NO_MEASURE_VALUE_1));
	}
	
	@Test
	public void testEncoderWithoutMeasureValue2() {
		iaMeasurePerformedNode.putValue("measurePerformed", null);
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}

		assertThat("expected encoder to return a json representation of an IA Section node", sw.toString(),
				is(EXPECTED_NO_MEASURE_VALUE_1));
	}

}
