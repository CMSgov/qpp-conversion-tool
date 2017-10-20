package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class AciProportionNumeratorDecoderTest {

	private static final String NUMERATOR_NODE_NAME = "aciProportionNumerator";
	private Node aciProportionNumeratorNode;
	private AciProportionNumeratorDecoder aciProportionNumeratorDecoder;
	private DecodeResult decodeResult;


	@Before
	public void setUp() {
		Element element = new Element("testElement");
		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorDecoder = new AciProportionNumeratorDecoder(new Context());
		decodeResult = aciProportionNumeratorDecoder.internalDecode(element, aciProportionNumeratorNode);
	}

	@Test
	public void testInternalDecodeObtainsCorrectDecodeResult() {
		assertWithMessage("Must continue on tree")
				.that(decodeResult)
				.isEquivalentAccordingToCompareTo(DecodeResult.TREE_CONTINUE);
	}

	@Test
	public void testInternalDecodeSetsCorrectNodeValue() {
		assertWithMessage("The node name must be %s", NUMERATOR_NODE_NAME)
				.that(aciProportionNumeratorNode.getValue("name")).isEqualTo(NUMERATOR_NODE_NAME);
	}
}
