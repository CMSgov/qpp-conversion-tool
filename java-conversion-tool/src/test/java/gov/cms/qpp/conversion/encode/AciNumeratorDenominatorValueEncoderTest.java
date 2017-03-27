package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;

public class AciNumeratorDenominatorValueEncoderTest {

	private Node numeratorDenominatorNode;
	private List<Node> nodes;

	public AciNumeratorDenominatorValueEncoderTest() {
	}

	@Before
	public void createNode() {
		numeratorDenominatorNode = new Node();
		numeratorDenominatorNode.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorDenominatorNode.putValue("aggregateCount", "600");

		nodes = new ArrayList<>();
		nodes.add(numeratorDenominatorNode);
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

		// NOTE: This test is only relevant in that it finds the deep value but it is not actually a result
		String EXPECTED = "{\n  \"value\" : 600\n}";
		assertThat("expected encoder to return a single number numerator/denominator", sw.toString(), is(EXPECTED));
	}

}
