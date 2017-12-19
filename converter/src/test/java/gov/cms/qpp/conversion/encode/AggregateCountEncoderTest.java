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
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class AggregateCountEncoderTest {

	private Node numeratorDenominatorNode;
	private List<Node> nodes;

	/**
	 * Set up a default node to be pass to an encoder
	 */
	@BeforeEach
	void createNode() {
		numeratorDenominatorNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorDenominatorNode.putValue("aggregateCount", "600");

		nodes = new ArrayList<>();
		nodes.add(numeratorDenominatorNode);
	}

	/**
	 * Test Function for the QppOutputEncoder
	 */
	@Test
	void testEncoderWithFramework() {
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}

		// NOTE: This test is only relevant in that it finds the deep value but it is not actually a result
		String EXPECTED = "{\n  \"value\" : 600\n}";
		assertWithMessage("expected encoder to return a single number numerator/denominator")
				.that(sw.toString())
				.isEqualTo(EXPECTED);
	}

	/**
	 * Test Function for the AggregateCountEncode
	 */
	@Test
	void testEncoder() {
		AggregateCountEncoder encoder = new AggregateCountEncoder(new Context());
		encoder.setNodes(nodes);
		JsonWrapper json = new JsonWrapper();
		try {
			encoder.internalEncode(json, numeratorDenominatorNode);
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}
		assertWithMessage("expected encoder to return a single number numerator/denominator")
				.that(json.getInteger("value"))
				.isEqualTo(600);
	}

}
