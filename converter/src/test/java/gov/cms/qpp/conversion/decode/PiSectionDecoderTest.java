package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

import static com.google.common.truth.Truth.assertThat;

class PiSectionDecoderTest {

	@Test
	void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		PiSectionDecoder piSectionDecoder = new PiSectionDecoder(new Context());
		piSectionDecoder.decode(element, node);

		assertThat(node.getValue("category"))
				.isEqualTo("pi");
	}
}
