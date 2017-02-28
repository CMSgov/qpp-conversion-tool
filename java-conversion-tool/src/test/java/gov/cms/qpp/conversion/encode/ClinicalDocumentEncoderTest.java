package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;

public class ClinicalDocumentEncoderTest {

	private static final String EXPECTED = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\"," + "\n  \"performanceYear\" : 2017,"
			+ "\n  \"measurementSets\" : [ " + "{\n    \"category\" : \"aci\",\n    \"measurements\" : [ "
			+ "{\n      \"measureId\" : \"ACI-PEA-1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    } ],"
			+ "\n    \"source\" : \"provider\"," + "\n    \"performanceStart\" : \"2017-01-01\","
			+ "\n    \"performanceEnd\" : \"2017-12-31\"" + "\n  } ]\n}";

	private Node aciSectionNode;
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private Node clinicalDocumentNode;
	private List<Node> nodes;

	public ClinicalDocumentEncoderTest() {
	}

	@Before
	public void createNode() {
		Validations.init();

		numeratorValueNode = new Node();
		numeratorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode.putValue("aciNumeratorDenominator", "400");

		denominatorValueNode = new Node();
		denominatorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode.putValue("aciNumeratorDenominator", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciSectionNode = new Node();
		aciSectionNode.setId("2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);

		clinicalDocumentNode = new Node();
		clinicalDocumentNode.setId("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");
		clinicalDocumentNode.addChildNode(aciSectionNode);

		nodes = new ArrayList<>();
		nodes.add(clinicalDocumentNode);
	}
	
	@After
	public void teardown() throws Exception {
		Validations.clear();
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

		assertThat("expected encoder to return a json representation of a clinical document node", sw.toString(),
				is(EXPECTED));
	}

}
