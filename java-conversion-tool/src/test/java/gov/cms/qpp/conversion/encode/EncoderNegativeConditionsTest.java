package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;

public class EncoderNegativeConditionsTest {

	public EncoderNegativeConditionsTest() {
	}

	@Test
	public void testNullEncoder() {
		QppOutputEncoder encoder = new QppOutputEncoder();

		Node aNode = new Node();
		aNode.setId("nothing");
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
		String EXPECTED = "null";
		assertThat("expected encoder to return an empty string", sw.toString(), is(EXPECTED));
	}

	@Test(expected = EncodeException.class)
	public void testException() throws EncodeException {
		Node numeratorDenominatorNode;
		List<Node> nodes;

		numeratorDenominatorNode = new Node();
		numeratorDenominatorNode.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorDenominatorNode.putValue("aggregateCount", "600");

		nodes = new ArrayList<>();
		nodes.add(numeratorDenominatorNode);

		QppOutputEncoder encoder = new QppOutputEncoder();

		encoder.setNodes(nodes);

		FailingWriter failWrite = new FailingWriter();

		encoder.encode(new BufferedWriter(failWrite));

	}

}
