package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

class AciProportionNumeratorDecoderTest {

	private static final String NUMERATOR_NODE_NAME = "aciProportionNumerator";
	private Node aciProportionNumeratorNode;
	private AciProportionNumeratorDecoder aciProportionNumeratorDecoder;
	private DecodeResult decodeResult;

	@BeforeEach
	void setUp() {
		Element element = new Element("testElement");
		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorDecoder = new AciProportionNumeratorDecoder(new Context());
		decodeResult = aciProportionNumeratorDecoder.internalDecode(element, aciProportionNumeratorNode);
	}

	@Test
	void testInternalDecodeObtainsCorrectDecodeResult() {
		assertWithMessage("Must continue on tree")
				.that(decodeResult)
				.isEquivalentAccordingToCompareTo(DecodeResult.TREE_CONTINUE);
	}

	@Test
	void testInternalDecodeSetsCorrectNodeValue() {
		assertWithMessage("The node name must be %s", NUMERATOR_NODE_NAME)
				.that(aciProportionNumeratorNode.getValue("name")).isEqualTo(NUMERATOR_NODE_NAME);
	}
}
