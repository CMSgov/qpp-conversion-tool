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

class PiNumeratorDenominatorEncoderTest {

	private static final String MEASURE_ID = "ACI-PEA-1";
	private Node piProportionMeasureNode;
	private Node piProportionNumeratorNode;
	private Node piProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private List<Node> nodes;

	@BeforeEach
	void createNode() {
		numeratorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		numeratorValueNode.putValue("aggregateCount", "400");

		denominatorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		denominatorValueNode.putValue("aggregateCount", "600");

		piProportionDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		piProportionDenominatorNode.addChildNode(denominatorValueNode);

		piProportionNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		piProportionNumeratorNode.addChildNode(numeratorValueNode);

		piProportionMeasureNode = new Node();
		piProportionMeasureNode.setType(TemplateId.PI_NUMERATOR_DENOMINATOR);
		piProportionMeasureNode.addChildNode(piProportionNumeratorNode);
		piProportionMeasureNode.addChildNode(piProportionDenominatorNode);
		piProportionMeasureNode.putValue("measureId", MEASURE_ID);

		nodes = new ArrayList<>();
		nodes.add(piProportionMeasureNode);
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
		PiNumeratorDenominatorEncoder objectUnderTest = new PiNumeratorDenominatorEncoder(new Context());

		//execute
		objectUnderTest.internalEncode(jsonWrapper, piProportionMeasureNode);

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
		PiNumeratorDenominatorEncoder objectUnderTest = new PiNumeratorDenominatorEncoder(new Context());
		Node unknownNode = new Node();
		piProportionMeasureNode.addChildNode(unknownNode);

		//execute
		objectUnderTest.internalEncode(jsonWrapper, piProportionMeasureNode);

		//assert
		assertThat(objectUnderTest.getDetails())
				.hasSize(1);
		assertWithMessage("The validation error must be the inability to find an encoder")
				.that(objectUnderTest.getDetails().get(0).getMessage())
				.isEqualTo(ErrorCode.CT_LABEL + "Failed to find an encoder");
	}
}
