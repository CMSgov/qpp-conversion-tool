package gov.cms.qpp.conversion.encode;

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

public class AciSectionEncoderMultiMeasureTest {

	private static final String EXPECTED = "{\n  \"category\" : \"aci\",\n  \"submissionMethod\" : \"electronicHealthRecord\",\n  "
			+ "\"measurements\" : [ "
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

	@Before
	public void createNode() {
		numeratorValueNode = new Node();
		numeratorValueNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorValueNode.putValue("aggregateCount", "400");

		numeratorValueNode2 = new Node();
		numeratorValueNode2.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorValueNode2.putValue("aggregateCount", "500");

		numeratorValueNode3 = new Node();
		numeratorValueNode3.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorValueNode3.putValue("aggregateCount", "400");

		denominatorValueNode = new Node();
		denominatorValueNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		denominatorValueNode.putValue("aggregateCount", "600");

		denominatorValueNode2 = new Node();
		denominatorValueNode2.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		denominatorValueNode2.putValue("aggregateCount", "700");

		denominatorValueNode3 = new Node();
		denominatorValueNode3.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		denominatorValueNode3.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node();
		aciProportionDenominatorNode2.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node();
		aciProportionDenominatorNode3.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId(TemplateId.ACI_NUMERATOR.getTemplateId());
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node();
		aciProportionNumeratorNode2.setId(TemplateId.ACI_NUMERATOR.getTemplateId());
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node();
		aciProportionNumeratorNode3.setId(TemplateId.ACI_NUMERATOR.getTemplateId());
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId(TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node();
		aciProportionMeasureNode2.setId(TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue("measureId", "ACI_EP_1");

		aciProportionMeasureNode3 = new Node();
		aciProportionMeasureNode3.setId(TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue("measureId", "ACI_CCTPE_3");

		aciSectionNode = new Node();
		aciSectionNode.setId(TemplateId.ACI_SECTION.getTemplateId());
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
