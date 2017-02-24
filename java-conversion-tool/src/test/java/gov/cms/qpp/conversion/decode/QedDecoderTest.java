package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class QedDecoderTest {

	@Test
	public void decodeQEDAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
				+ "	<observation resultName=\"result\" resultValue=\"mytestvalue\">\n"
				+ "		<templateId root=\"Q.E.D\"/>\n"
				+ "	</observation>"
				+ "</root>";
				

		Element dom = XmlUtils.stringToDOM(xmlFragment);

		QppXmlDecoder decoder = new QppXmlDecoder();
		decoder.setDom(dom);

		// Get the root wrapper node
		Node root = decoder.decode();
		assertThat("root node should not be null", root, is(not(nullValue())));
		// Make sure we get have target
		assertThat("root node should have one child node", root.getChildNodes().size(), is(1));

		Node target = root.getChildNodes().get(0);

		assertThat("test value should be mytestvalue", target.getValue("result"), is("mytestvalue"));

	}

}
