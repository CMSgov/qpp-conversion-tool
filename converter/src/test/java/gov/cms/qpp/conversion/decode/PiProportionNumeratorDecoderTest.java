package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

import static com.google.common.truth.Truth.assertThat;

class PiProportionNumeratorDecoderTest {

	@Test
	void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		PiProportionNumeratorDecoder piProportionNumeratorDecoder = new PiProportionNumeratorDecoder(new Context());
		DecodeResult decodeResult = piProportionNumeratorDecoder.decode(element, node);

		assertThat(decodeResult)
				.isEqualTo(DecodeResult.TREE_CONTINUE);
		assertThat(node.getValue("name")).isEqualTo(PiProportionNumeratorDecoder.NUMERATOR_NODE_NAME);
	}
}
