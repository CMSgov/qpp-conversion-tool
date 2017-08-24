package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

public class EncoderNegativeConditionsTest {

	@Test
	public void testNullEncoder() {
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
			fail("Failure to encode: " + e.getMessage());
		}

		// NOTE: This test is only relevant in that it finds the deep value but
		// it is not actually a result
		String expected = "null";
		assertThat("expected encoder to return an empty string", sw.toString(), is(expected));
	}

	@Test
	public void testExceptionAddsValidation() throws EncodeException {
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

		assertThat("Should contain one error", encoder.getDetails(), hasSize(1));
		assertThat("Should have same correct message", encoder.getDetails().get(0).getMessage(),
				is("Failure to encode"));
	}
}
