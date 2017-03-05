package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;

public class IaSectionEncoderTest {

	private static final String EXPECTED = "{\n  \"category\" : \"ia\",\n  \"measurements\" : [ "
			+ "{\n    \"measureId\" : \"IA_EPA_1\",\n    \"value\" : true\n  } ]\n}";
	private static final String EXPECTED_NO_MEASURE = "{\n  \"category\" : \"ia\"\n}";
	private static final String EXPECTED_NO_MEASURE_VALUE_1 = "{\n  \"category\" : \"ia\",\n  \"measurements\" : [ "
			+ "{\n    \"measureId\" : \"IA_EPA_1\"\n  } ]\n}";

	private Node iaSectionNode;
	private Node iaMeasureNode;
	private Node iaMeasurePerformedNode;
	private List<Node> nodes;

	public IaSectionEncoderTest() {
	}

	@Before
	public void createNode() {
		iaMeasurePerformedNode = new Node();
		iaMeasurePerformedNode.setId("2.16.840.1.113883.10.20.27.3.27");
		iaMeasurePerformedNode.putValue("measurePerformed", "Y");

		iaMeasureNode = new Node();
		iaMeasureNode.setId("2.16.840.1.113883.10.20.27.3.33");
		iaMeasureNode.putValue("measureId", "IA_EPA_1");
		iaMeasureNode.addChildNode(iaMeasurePerformedNode);

		iaSectionNode = new Node();
		iaSectionNode.setId("2.16.840.1.113883.10.20.27.2.4");
		iaSectionNode.putValue("category", "ia");
		iaSectionNode.addChildNode(iaMeasureNode);

		nodes = new ArrayList<>();
		nodes.add(iaSectionNode);
	}

	@Test
	public void testEncoder() {
		QppOutputEncoder encoder = new QppOutputEncoder();

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
		QppOutputEncoder encoder = new QppOutputEncoder();

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
		QppOutputEncoder encoder = new QppOutputEncoder();

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
		QppOutputEncoder encoder = new QppOutputEncoder();

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
