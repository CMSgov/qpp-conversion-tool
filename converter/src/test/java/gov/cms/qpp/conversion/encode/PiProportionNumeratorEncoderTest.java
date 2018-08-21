package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class PiProportionNumeratorEncoderTest {

	private Node piProportionNumeratorNode;
	private Node numeratorDenominatorValueNode;
	private JsonWrapper jsonWrapper;

	@BeforeEach
	void createNode() {
		Node ensureChildOrderIsNotProblematic = new Node(TemplateId.CLINICAL_DOCUMENT);

		numeratorDenominatorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		numeratorDenominatorValueNode.putValue("aggregateCount", "600");

		piProportionNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		piProportionNumeratorNode.addChildNode(ensureChildOrderIsNotProblematic);
		piProportionNumeratorNode.addChildNode(numeratorDenominatorValueNode);

		jsonWrapper = new JsonWrapper();
	}

	@Test
	void testInternalEncode() throws EncodeException {
		PiProportionNumeratorEncoder piProportionNumeratorEncoder = new PiProportionNumeratorEncoder(new Context());
		piProportionNumeratorEncoder.internalEncode(jsonWrapper, piProportionNumeratorNode);

		assertThat(jsonWrapper.getInteger("numerator"))
				.isEqualTo(600);
	}

	@Test
	void testEncoderWithoutChild() throws EncodeException {
		piProportionNumeratorNode.getChildNodes().remove(numeratorDenominatorValueNode);

		PiProportionNumeratorEncoder piProportionNumeratorEncoder = new PiProportionNumeratorEncoder(new Context());
		piProportionNumeratorEncoder.internalEncode(jsonWrapper, piProportionNumeratorNode);

		assertThat(jsonWrapper.getInteger("numerator"))
				.isNull();
	}

	@Test
	void testEncoderWithoutValue() throws EncodeException {
		numeratorDenominatorValueNode.putValue("aggregateCount", null);

		PiProportionNumeratorEncoder piProportionNumeratorEncoder = new PiProportionNumeratorEncoder(new Context());
		piProportionNumeratorEncoder.internalEncode(jsonWrapper, piProportionNumeratorNode);

		assertThat(jsonWrapper.getInteger("numerator"))
				.isNull();
	}
}
