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

public class AciProportionNumeratorEncoderTest {

	private Node aciProportionNumeratorNode;
	private Node numeratorDenominatorValueNode;
	private List<Node> nodes;

	public AciProportionNumeratorEncoderTest() {
	}

	@Before
	public void createNode() {
		numeratorDenominatorValueNode = new Node();
		numeratorDenominatorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorDenominatorValueNode.putValue("aggregateCount", "600");

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode.addChildNode(numeratorDenominatorValueNode);

		nodes = new ArrayList<>();
		nodes.add(aciProportionNumeratorNode);
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

		String EXPECTED = "{\n  \"numerator\" : 600\n}";
		assertThat("expected encoder to return a json representation of a numerator with a value", sw.toString(),
				is(EXPECTED));
	}

}
