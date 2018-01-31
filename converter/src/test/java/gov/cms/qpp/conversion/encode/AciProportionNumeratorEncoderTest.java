package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class AciProportionNumeratorEncoderTest {

	private Node aciProportionNumeratorNode;
	private Node numeratorDenominatorValueNode;
	private JsonWrapper jsonWrapper;

	@BeforeEach
	void createNode() {
		Node ensureChildOrderIsNotProblematic = new Node(TemplateId.CLINICAL_DOCUMENT);

		numeratorDenominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorDenominatorValueNode.putValue("aggregateCount", "600");

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(ensureChildOrderIsNotProblematic);
		aciProportionNumeratorNode.addChildNode(numeratorDenominatorValueNode);

		jsonWrapper = new JsonWrapper();
	}

	@Test
	void testInternalEncode() throws EncodeException {
		AciProportionNumeratorEncoder aciProportionNumeratorEncoder = new AciProportionNumeratorEncoder(new Context());
		aciProportionNumeratorEncoder.internalEncode(jsonWrapper, aciProportionNumeratorNode);

		assertThat(jsonWrapper.getInteger("numerator"))
				.isEqualTo(600);
	}

	@Test
	void testEncoderWithoutChild() throws EncodeException {
		aciProportionNumeratorNode.getChildNodes().remove(numeratorDenominatorValueNode);

		AciProportionNumeratorEncoder aciProportionNumeratorEncoder = new AciProportionNumeratorEncoder(new Context());
		aciProportionNumeratorEncoder.internalEncode(jsonWrapper, aciProportionNumeratorNode);

		assertThat(jsonWrapper.getInteger("numerator"))
				.isNull();
	}

	@Test
	void testEncoderWithoutValue() throws EncodeException {
		numeratorDenominatorValueNode.putValue("aggregateCount", null);

		AciProportionNumeratorEncoder aciProportionNumeratorEncoder = new AciProportionNumeratorEncoder(new Context());
		aciProportionNumeratorEncoder.internalEncode(jsonWrapper, aciProportionNumeratorNode);

		assertThat(jsonWrapper.getInteger("numerator"))
				.isNull();
	}
}
