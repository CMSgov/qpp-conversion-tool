package gov.cms.qpp.conversion.decode;

import static com.google.common.truth.Truth.assertWithMessage;

import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

class AciSectionDecoderTest {

	@Test
	void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciSectionDecoder aciSectionDecoder = new AciSectionDecoder(new Context());
		aciSectionDecoder.internalDecode(element, node);

		assertWithMessage("Node Category must be aci")
				.that(node.getValue("category"))
				.isEqualTo("aci");
	}
}
