package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class AciProportionDenominatorEncoderTest {

	private Node aciProportionDenominatorNode;
	private Node numeratorDenominatorValueNode;
	private List<Node> nodes;
	private JsonWrapper json;

	@BeforeEach
	void createNode() {
		numeratorDenominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorDenominatorValueNode.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(numeratorDenominatorValueNode);

		nodes = new ArrayList<>();
		nodes.add(aciProportionDenominatorNode);

		json = new JsonWrapper();
	}

	@Test
	void testEncoder() {
		runEncoder();

		assertWithMessage("denominator value must be 600")
				.that(json.getInteger("denominator"))
				.isEqualTo(600);
	}

	@Test
	void testEncoderWithoutChild() {
		aciProportionDenominatorNode.getChildNodes().remove(numeratorDenominatorValueNode);
		runEncoder();

		assertWithMessage("denominator value must be null")
				.that(json.getInteger("denominator"))
				.isNull();
	}

	@Test
	void testEncoderWithoutValue() {
		numeratorDenominatorValueNode.putValue("aggregateCount", null);
		runEncoder();

		assertWithMessage("expected encoder to return null")
				.that(json.toString())
				.isEqualTo("null");
	}

	private void runEncoder() {
		AciProportionDenominatorEncoder encoder = new AciProportionDenominatorEncoder(new Context());
		try {
			encoder.internalEncode(json, aciProportionDenominatorNode);
		} catch (EncodeException e) {
			throw new RuntimeException(e);
		}
		encoder.setNodes(nodes);
	}
}
