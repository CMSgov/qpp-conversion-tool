package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

import static com.google.common.truth.Truth.assertThat;

class AciProportionNumeratorDecoderTest {

	private static final String NUMERATOR_NODE_NAME = "aciProportionNumerator";

	@Test
	void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciProportionNumeratorDecoder aciProportionNumeratorDecoder = new AciProportionNumeratorDecoder(new Context());
		DecodeResult decodeResult = aciProportionNumeratorDecoder.decode(element, node);

		assertThat(decodeResult)
				.isEqualTo(DecodeResult.TREE_CONTINUE);
		assertThat(node.getValue("name")).isEqualTo(NUMERATOR_NODE_NAME);
	}
}
