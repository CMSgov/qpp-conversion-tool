package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class IaSectionDecoderTest {

	@Test
	public void decodeAciSectionAsNode() throws Exception {
		String xmlFragment = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
				"<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n" +
				"	<section>\n" + 
				"		<!-- Measure Section -->\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.24.2.2\"/>\n" + 
				"		<!-- Improvement Activity Section templateId -->\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.27.2.4\" extension=\"2016-09-01\"/>\n" + 
				"		<code code=\"55186-1\" codeSystem=\"2.16.840.1.113883.6.1\" displayName=\"Measure Section\"/>\n" + 
				"		<entry>\n" + 
				"			<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">\n" + 
				"				<!-- Implied template Measure Reference templateId -->\n" + 
				"				<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/>\n" + 
				"				<!-- Improvement Activity Performed Reference and Results templateId -->\n" + 
				"				<templateId root=\"2.16.840.1.113883.10.20.27.3.33\" extension=\"2016-09-01\"/>\n" + 
				"				<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>\n" + 
				"				<statusCode code=\"completed\"/>\n" + 
				"				<reference typeCode=\"REFR\">\n" + 
				"					<!-- Reference to a particular ACI measure's unique identifier. -->\n" + 
				"					<externalDocument classCode=\"DOC\" moodCode=\"EVN\">\n" + 
				"						<!-- This is a temporary root OID that indicates this is an improvement activity identifier -->\n" + 
				"						<!-- extension is the unique identifier for an improvement activity. \"IA_EPA_1\" is for illustration only. -->\n" + 
				"						<id root=\"2.16.840.1.113883.3.7034\" extension=\"IA_EPA_1\"/>\n" + 
				"						<!-- Improvement activity narrative text (for reference) -->\n" + 
				"						<text> Collection of patient experience and satisfaction data on										access to care and development of an improvement plan, such										as outlining steps for improving communications with										patients to help understanding of urgent access needs.									</text>\n" + 
				"					</externalDocument>\n" + 
				"				</reference>\n" + 
				"				<component>\n" + 
				"					<observation classCode=\"OBS\" moodCode=\"EVN\">\n" + 
				"						<!-- Measure Performed templateId -->\n" + 
				"						<templateId root=\"2.16.840.1.113883.10.20.27.3.27\" extension=\"2016-09-01\"/>\n" + 
				"						<code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>\n" + 
				"						<statusCode code=\"completed\"/>\n" + 
				"						<value xsi:type=\"CD\" code=\"Y\" displayName=\"Yes\" codeSystemName=\"Yes/no indicator (HL7 Table 0136)\" codeSystem=\"2.16.840.1.113883.12.136\"/>\n" + 
				"					</observation>\n" + 
				"				</component>\n" + 
				"			</organizer>\n" + 
				"		</entry>" + 
				"	</section>\n" + 
				"</component>";

		Node root = new QppXmlDecoder().decodeFragment(XmlUtils.stringToDOM(xmlFragment));

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));
		
		// Should have a section node 
		assertThat("returned node should not be null", root.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", root.getChildNodes().size(), is(1));
		Node iaSectionNode = root.getChildNodes().get(0);
		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));

		// Should have a Measure node 
		assertThat("returned node should not be null", iaSectionNode.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", iaSectionNode.getChildNodes().size(), is(1));
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertThat("returned should have measureId", iaMeasureNode.getValue("measureId"), is("IA_EPA_1"));
		
		// Should have a measure performed node 
		assertThat("returned node should not be null", iaMeasureNode.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", iaMeasureNode.getChildNodes().size(), is(1));
		Node iaMeasurePerformedNode = iaMeasureNode.getChildNodes().get(0);
		assertThat("returned measurePerformed", iaMeasurePerformedNode.getValue("measurePerformed"), is("Y"));

	}

}
