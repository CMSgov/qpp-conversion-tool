package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;

class AciNumeratorDenominatorEncoderTest {

	private static final String MEASURE_ID = "ACI-PEA-1";
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private List<Node> nodes;

	@BeforeEach
	void createNode() {
		numeratorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode.putValue("aggregateCount", "400");

		denominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setType(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", MEASURE_ID);

		nodes = new ArrayList<>();
		nodes.add(aciProportionMeasureNode);
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

		String EXPECTED = "{\n  \"measureId\" : \"" + MEASURE_ID + "\",\n  \"value\" : {\n    \"numerator\" : 400,\n    \"denominator\" : 600\n  }\n}";
		assertThat(sw.toString())
				.isEqualTo(EXPECTED);
	}

	@Test
	void testInternalEncode() throws EncodeException {

		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciNumeratorDenominatorEncoder objectUnderTest = new AciNumeratorDenominatorEncoder(new Context());

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciProportionMeasureNode);

		//assert
		assertThat(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertThat(jsonWrapper.getObject())
				.isNotNull();
		assertThat(jsonWrapper.getObject())
				.isInstanceOf(Map.class);
		assertThat(((Map<?, ?>)jsonWrapper.getObject()).get("value"))
				.isNotNull();
	}

	@Test
	void testNoChildEncoder() throws EncodeException {
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciNumeratorDenominatorEncoder objectUnderTest = new AciNumeratorDenominatorEncoder(new Context());
		Node unknownNode = new Node();
		aciProportionMeasureNode.addChildNode(unknownNode);

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciProportionMeasureNode);

		//assert
		assertThat(objectUnderTest.getDetails())
				.hasSize(1);
		assertWithMessage("The validation error must be the inability to find an encoder")
				.that(objectUnderTest.getDetails().get(0).getMessage())
				.isEqualTo(ErrorCode.CT_LABEL + "Failed to find an encoder");
	}
}
