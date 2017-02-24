package gov.cms.qpp.conversion.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class QEDParserTest {

	@Test
	public void parseQEDAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
				+ "	<qed resultName=\"result\" resultValue=\"mytestvalue\">\n"
				+ "		<templateId root=\"Q.E.D\"/>\n"
				+ "	</qed>"
				+ "</root>";
				

		Element dom = XmlUtils.stringToDOM(xmlFragment);

		QppXmlInputParser parser = new QppXmlInputParser();
		parser.setDom(dom);

		// Get the root wrapper node
		Node root = parser.parse();
		assertThat("root node should not be null", root, is(not(nullValue())));
		// Make sure we get have target
		assertThat("root node should have one child node", root.getChildNodes().size(), is(1));

		Node target = root.getChildNodes().get(0);

		assertThat("test value should be mytestvalue", target.get("result"), is("mytestvalue"));

	}

}
