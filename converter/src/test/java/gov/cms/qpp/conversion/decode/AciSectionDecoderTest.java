package gov.cms.qpp.conversion.decode;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

public class AciSectionDecoderTest {

	@Test
	public void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciSectionDecoder aciSectionDecoder = new AciSectionDecoder(new Context());
		aciSectionDecoder.internalDecode(element, node);

		assertWithMessage("Node Category must be aci")
				.that(node.getValue("category"))
				.isEqualTo("aci");
	}
}
