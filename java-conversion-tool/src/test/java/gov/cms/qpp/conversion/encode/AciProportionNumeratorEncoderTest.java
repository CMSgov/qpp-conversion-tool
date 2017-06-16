package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class AciProportionNumeratorEncoderTest {

	private Node aciProportionNumeratorNode;
	private Node numeratorDenominatorValueNode;
	private List<Node> nodes;
	private JsonWrapper jsonWrapper;

	@Before
	public void createNode() {
		numeratorDenominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorDenominatorValueNode.putValue("aggregateCount", "600");

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorDenominatorValueNode);

		nodes = new ArrayList<>();
		nodes.add(aciProportionNumeratorNode);

		jsonWrapper = new JsonWrapper();
	}

	@Test
	public void testInternalEncode() throws EncodeException {
		AciProportionNumeratorEncoder aciProportionNumeratorEncoder = new AciProportionNumeratorEncoder();
		aciProportionNumeratorEncoder.internalEncode(jsonWrapper, aciProportionNumeratorNode);

		assertThat("Must have a numerator value of 600", 600, is(jsonWrapper.getInteger("numerator")));
	}

	@Test
	public void testEncoderWithoutChild() throws EncodeException {
		aciProportionNumeratorNode.getChildNodes().remove(numeratorDenominatorValueNode);

		AciProportionNumeratorEncoder aciProportionNumeratorEncoder = new AciProportionNumeratorEncoder();
		aciProportionNumeratorEncoder.internalEncode(jsonWrapper, aciProportionNumeratorNode);

		assertThat("Must have a null numerator", null, is(jsonWrapper.getInteger("numerator")));
	}

	@Test
	public void testEncoderWithoutValue() throws EncodeException {
		numeratorDenominatorValueNode.putValue("aggregateCount", null);

		AciProportionNumeratorEncoder aciProportionNumeratorEncoder = new AciProportionNumeratorEncoder();
		aciProportionNumeratorEncoder.internalEncode(jsonWrapper, aciProportionNumeratorNode);

		assertThat("Must have a numerator value of null", null, is(jsonWrapper.getInteger("numerator")));
	}
}
