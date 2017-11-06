package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class AciProportionNumeratorDecoderTest {

	private static final String NUMERATOR_NODE_NAME = "aciProportionNumerator";

	@Test
	void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciProportionNumeratorDecoder aciProportionNumeratorDecoder = new AciProportionNumeratorDecoder(new Context());
		DecodeResult decodeResult = aciProportionNumeratorDecoder.internalDecode(element, node);

		assertWithMessage("Must continue on tree")
				.that(decodeResult)
				.isEquivalentAccordingToCompareTo(DecodeResult.TREE_CONTINUE);
		assertWithMessage("The node name must be %s", NUMERATOR_NODE_NAME)
				.that(node.getValue("name")).isEqualTo(NUMERATOR_NODE_NAME);
	}
}
