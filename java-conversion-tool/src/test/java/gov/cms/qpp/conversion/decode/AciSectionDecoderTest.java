package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AciSectionDecoderTest {

	@Test
	public void decodeAciSectionAsNode() throws Exception {
		String xmlFragment = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
				"<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n" +
				"	<section>\n" + 
				"		<!-- Measure Section -->\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.24.2.2\"/>\n" + 
				"		<!-- Advancing Care Information Section templateId -->\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.27.2.5\" extension=\"2016-09-01\"/>\n" + 
				"		<code code=\"55186-1\" codeSystem=\"2.16.840.1.113883.6.1\" displayName=\"Measure Section\"/>\n" + 
				"		<title>Measure Section</title>\n" + 
				"		<text>\n" + 
				"		</text>\n" + 
				"		<entry>\n" + 
				"			<qed resultName=\"measure\" resultValue=\"measure1\">\n" +
				"				<templateId root=\"Q.E.D\"/>\n" +
				"			</qed>" +
				"		</entry>\n" + 
				"	</section>\n" + 
				"</component>";

		Node root = new QppXmlDecoder().decodeFragment(XmlUtils.stringToDOM(xmlFragment));

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));
		Node aciSectionNode = root.getChildNodes().get(0);
		// Should have a section node 
		assertThat("returned category should be aci", aciSectionNode.getValue("category"), is("aci"));
		// Should have a section node 
		assertThat("returned node should have one child decoder node", aciSectionNode.getChildNodes().size(), is(1));
		// Should have a section node 
		assertThat("returned QED child should have measure measure1", aciSectionNode.getChildNodes().get(0).getValue("measure"), is("measure1"));

	}

}
