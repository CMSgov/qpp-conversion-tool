package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ReportingParametersSectionDecoderTest {

	@Test
	public void decodeAciSectionAsNode() throws Exception {
		String xmlFragment = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
				"<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n" +
				"	<section>\r\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.17.2.1\"/>\r\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.27.2.2\"/>\r\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.27.2.6\" extension=\"2016-11-01\"/>\r\n" + 
				"		<code code=\"55187-9\" codeSystem=\"2.16.840.1.113883.6.1\"/>\r\n" + 
				"		<title>Reporting Parameters</title>\r\n" + 
				"		<text>\r\n" + 
				"			<list>\r\n" + 
				"				<item>Reporting period: 01 January 2017 - 31 December 2017</item>\r\n" + 
				"			</list>\r\n" + 
				"		</text>\r\n" + 
				"		<entry typeCode=\"DRIV\">\r\n" + 
				"			<act classCode=\"ACT\" moodCode=\"EVN\">\r\n" + 
				"				<templateId root=\"2.16.840.1.113883.10.20.17.3.8\"/>\r\n" + 
				"				<templateId root=\"2.16.840.1.113883.10.20.27.3.23\" extension=\"2016-11-01\"/>\r\n" + 
				"				<id root=\"95944FB8-241B-11E5-1027-09173F13E4C5\"/>\r\n" + 
				"				<code code=\"252116004\" codeSystem=\"2.16.840.1.113883.6.96\" displayName=\"Observation Parameters\"/>\r\n" + 
				"				<effectiveTime>\r\n" + 
				"					<low value=\"20170101\"/>\r\n" + 
				"					<high value=\"20171231\"/>\r\n" + 
				"				</effectiveTime>\r\n" + 
				"			</act>\r\n" + 
				"		</entry>\r\n" + 
				"	</section>\n" + 
				"</component>";

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));
		
		// Should have a section node 
		assertThat("returned node should not be null", root.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", root.getChildNodes().size(), is(1));
		Node reportingSectionNode = root.getChildNodes().get(0);
		assertThat("returned category", reportingSectionNode.getValue("source"), is("provider"));

		// Should have a Measure node 
		assertThat("returned node should not be null", reportingSectionNode.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", reportingSectionNode.getChildNodes().size(), is(1));
		Node reportingActSectionNodeMeasureNode = reportingSectionNode.getChildNodes().get(0);
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceStart"), is("20170101"));
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceEnd"), is("20171231"));
	}

}
