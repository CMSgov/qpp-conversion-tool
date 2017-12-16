package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class AciSectionEncoderMultiMeasureTest {

	private static final String EXPECTED = "{\n  \"category\" : \"aci\",\n  \"submissionMethod\" : \"electronicHealthRecord\",\n  "
			+ "\"measurements\" : [ "
			+ "{\n    \"measureId\" : \"ACI-PEA-1\",\n    \"value\" : {\n"
			+ "      \"numerator\" : 400,\n      \"denominator\" : 600\n    }\n  }, "
			+ "{\n    \"measureId\" : \"ACI_EP_1\",\n    \"value\" : {\n"
			+ "      \"numerator\" : 500,\n      \"denominator\" : 700\n    }\n  }, "
			+ "{\n    \"measureId\" : \"ACI_CCTPE_3\",\n    \"value\" : {\n"
			+ "      \"numerator\" : 400,\n      \"denominator\" : 600\n    }\n  }" + " ],\n  \"performanceStart\" : \"2017-01-01\",\n  \"performanceEnd\" : \"2017-12-31\"\n}";

	private Node aciSectionNode;
	private Node reportingParametersNode;
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

	@BeforeEach
	void createNode() {
		numeratorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode.putValue("aggregateCount", "400");

		numeratorValueNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode2.putValue("aggregateCount", "500");

		numeratorValueNode3 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode3.putValue("aggregateCount", "400");

		denominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode.putValue("aggregateCount", "600");

		denominatorValueNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode2.putValue("aggregateCount", "700");

		denominatorValueNode3 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode3.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue("measureId", "ACI_EP_1");

		aciProportionMeasureNode3 = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue("measureId", "ACI_CCTPE_3");

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);
		aciSectionNode.addChildNode(aciProportionMeasureNode2);
		aciSectionNode.addChildNode(aciProportionMeasureNode3);

		reportingParametersNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START,"20170101");
		reportingParametersNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END,"20171231");
		aciSectionNode.addChildNode(reportingParametersNode);

		nodes = new ArrayList<>();
		nodes.add(aciSectionNode);
	}

	@Test
	void testEncoder() {
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}

		assertWithMessage("expected encoder to return a json representation of an ACI Section node")
				.that(sw.toString())
				.isEqualTo(EXPECTED);
	}

}
