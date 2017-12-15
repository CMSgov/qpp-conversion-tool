package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class EncoderNegativeConditionsTest {

	@Test
	void testNullEncoder() {
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		Node aNode = new Node();
		aNode.putValue("something", "600");

		List<Node> nodes = new ArrayList<>();
		nodes.add(aNode);

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}

		// NOTE: This test is only relevant in that it finds the deep value but
		// it is not actually a result
		String expected = "null";
		assertWithMessage("expected encoder to return an empty string")
				.that(sw.toString())
				.isEqualTo(expected);
	}

	@Test
	void testExceptionAddsValidation() throws EncodeException {
		Node numeratorDenominatorNode;
		List<Node> nodes;

		numeratorDenominatorNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorDenominatorNode.putValue("aggregateCount", "600");

		nodes = new ArrayList<>();
		nodes.add(numeratorDenominatorNode);

		QppOutputEncoder encoder = new QppOutputEncoder(new Context());

		encoder.setNodes(nodes);

		FailingWriter failWrite = new FailingWriter();

		encoder.encode(new BufferedWriter(failWrite));

		assertWithMessage("Should contain one error").that(encoder.getDetails()).hasSize(1);
		assertWithMessage("Should have same correct message")
				.that(encoder.getDetails().get(0).getMessage())
				.isEqualTo("Fake IOException");
	}
}
