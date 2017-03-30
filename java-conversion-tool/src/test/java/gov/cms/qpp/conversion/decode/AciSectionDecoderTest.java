package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jdom2.Element;
import org.junit.Ignore;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AciSectionDecoderTest {

	@Test
	public void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciSectionDecoder aciSectionDecoder = new AciSectionDecoder();
		aciSectionDecoder.internalDecode(element, node);

		assertThat("Node Category must be aci",node.getValue("category"),is("aci"));
	}

}
