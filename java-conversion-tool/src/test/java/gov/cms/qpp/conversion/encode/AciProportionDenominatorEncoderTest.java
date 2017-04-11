package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validations;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class AciProportionDenominatorEncoderTest {

	private Node aciProportionDenominatorNode;
	private Node numeratorDenominatorValueNode;
	private List<Node> nodes;
	private JsonWrapper json;

	@Before
	public void createNode() {
		numeratorDenominatorValueNode = new Node();
		numeratorDenominatorValueNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorDenominatorValueNode.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode.addChildNode(numeratorDenominatorValueNode);

		nodes = new ArrayList<>();
		nodes.add(aciProportionDenominatorNode);

		json = new JsonWrapper();
		Validations.init();
	}

	@Test
	public void testEncoder() {
		runEncoder();

		assertThat("denominator value must be 600", json.getInteger("denominator"), is(600));
	}

	@Test
	public void testEncoderWithoutChild() {
		aciProportionDenominatorNode.getChildNodes().remove(numeratorDenominatorValueNode);
		runEncoder();

		assertNull("denominator value must be null", json.getInteger("denominator"));
	}

	@Test
	public void testEncoderWithoutValue() {
		numeratorDenominatorValueNode.putValue("aggregateCount", null);
		runEncoder();

		assertThat("expected encoder to return null", json.toString(), is("null"));
	}

	private void runEncoder() {
		AciProportionDenominatorEncoder encoder = new AciProportionDenominatorEncoder();
		try {
			encoder.internalEncode(json, aciProportionDenominatorNode);
		} catch (EncodeException e) {
			throw new RuntimeException(e);
		}
		encoder.setNodes(nodes);
	}
}
