package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import gov.cms.qpp.conversion.model.Validations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;

public class AciProportionNumeratorEncoderTest {

	private Node aciProportionNumeratorNode;
	private Node numeratorDenominatorValueNode;
	private List<Node> nodes;
	private JsonWrapper jsonWrapper;

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

		jsonWrapper = new JsonWrapper();
		Validations.init();
	}

	@After
	public void afterTests() {
		Validations.clear();
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
