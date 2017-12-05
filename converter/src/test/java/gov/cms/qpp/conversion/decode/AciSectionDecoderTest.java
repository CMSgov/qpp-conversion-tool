package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class AciSectionDecoderTest {

	@Test
	void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciSectionDecoder aciSectionDecoder = new AciSectionDecoder(new Context());
		aciSectionDecoder.internalDecode(element, node);

		assertThat(node.getValue("category"))
				.isEqualTo("aci");
	}
}
