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

public class AciSectionEncoderMultiMeasureTest {

	private static final String EXPECTED = "{\n  \"category\" : \"aci\",\n  \"measurements\" : [ "
			+ "{\n    \"measureId\" : \"ACI-PEA-1\",\n    \"value\" : {\n"
			+ "      \"numerator\" : 400,\n      \"denominator\" : 600\n    }\n  }, "
			+ "{\n    \"measureId\" : \"ACI_EP_1\",\n    \"value\" : {\n"
			+ "      \"numerator\" : 500,\n      \"denominator\" : 700\n    }\n  }, "
			+ "{\n    \"measureId\" : \"ACI_CCTPE_3\",\n    \"value\" : {\n"
			+ "      \"numerator\" : 400,\n      \"denominator\" : 600\n    }\n  }" + " ]\n" + "}";

	private Node aciSectionNode;
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private Node aciProportionMeasureNode2;
	private Node aciProportionNumeratorNode2;
	private Node aciProportionDenominatorNode2;
	private Node numeratorValueNode2;
	private Node denominatorValueNode2;
	private Node aciProportionMeasureNode3;
	private Node aciProportionNumeratorNode3;
	private Node aciProportionDenominatorNode3;
	private Node numeratorValueNode3;
	private Node denominatorValueNode3;
	private List<Node> nodes;

	public AciSectionEncoderMultiMeasureTest() {
	}

	@Before
	public void createNode() {
		numeratorValueNode = new Node();
		numeratorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode.putValue("aciNumeratorDenominator", "400");

		numeratorValueNode2 = new Node();
		numeratorValueNode2.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode2.putValue("aciNumeratorDenominator", "500");

		numeratorValueNode3 = new Node();
		numeratorValueNode3.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode3.putValue("aciNumeratorDenominator", "400");

		denominatorValueNode = new Node();
		denominatorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode.putValue("aciNumeratorDenominator", "600");

		denominatorValueNode2 = new Node();
		denominatorValueNode2.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode2.putValue("aciNumeratorDenominator", "700");

		denominatorValueNode3 = new Node();
		denominatorValueNode3.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode3.putValue("aciNumeratorDenominator", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node();
		aciProportionDenominatorNode2.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node();
		aciProportionDenominatorNode3.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node();
		aciProportionNumeratorNode2.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node();
		aciProportionNumeratorNode3.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node();
		aciProportionMeasureNode2.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue("measureId", "ACI_EP_1");

		aciProportionMeasureNode3 = new Node();
		aciProportionMeasureNode3.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue("measureId", "ACI_CCTPE_3");

		aciSectionNode = new Node();
		aciSectionNode.setId("2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);
		aciSectionNode.addChildNode(aciProportionMeasureNode2);
		aciSectionNode.addChildNode(aciProportionMeasureNode3);

		nodes = new ArrayList<>();
		nodes.add(aciSectionNode);
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

		assertThat("expected encoder to return a json representation of an ACI Section node", sw.toString(),
				is(EXPECTED));
	}

}
