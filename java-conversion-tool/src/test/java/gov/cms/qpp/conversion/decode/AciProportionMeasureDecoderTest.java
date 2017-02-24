package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AciProportionMeasureDecoderTest {

	@Test
	public void decodeACIProportionMeasureAsNode() throws Exception {
		String xmlFragment = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
				"<entry xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + 
				"	<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">\n" + 
				"		<!-- Implied template Measure Reference templateId -->\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/>\n" + 
				"		<!-- ACI Numerator Denominator Type Measure Reference and Results templateId -->\n" + 
				"		<templateId root=\"2.16.840.1.113883.10.20.27.3.28\" extension=\"2016-09-01\"/>\n" + 
				"		<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>\n" + 
				"		<statusCode code=\"completed\"/>\n" + 
				"		<reference typeCode=\"REFR\">\n" + 
				"			<!-- Reference to a particular ACI measure's unique identifier. -->\n" + 
				"			<externalDocument classCode=\"DOC\" moodCode=\"EVN\">\n" + 
				"				<!-- This is a temporary root OID that indicates this is an ACI measure identifier -->\n" + 
				"				<!-- extension is the unique identifier for an ACI measure. \"ACI-PEA-1\" is for illustration only. -->\n" + 
				"				<id root=\"2.16.840.1.113883.3.7031\" extension=\"ACI-PEA-1\"/>\n" + 
				"				<!-- ACI measure title -->\n" + 
				"				<text>Patient Access</text>\n" + 
				"			</externalDocument>\n" + 
				"		</reference>\n" + 
				"		<component>\n" + 
				"			<observation classCode=\"OBS\" moodCode=\"EVN\">\n" + 
				"				<!-- Performance Rate templateId -->\n" + 
				"				<templateId root=\"2.16.840.1.113883.10.20.27.3.30\"\n" + 
				"					extension=\"2016-09-01\"/>\n" + 
				"				<code code=\"72510-1\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Performance Rate\"/>\n" + 
				"				<statusCode code=\"completed\"/>\n" + 
				"				<value xsi:type=\"REAL\" value=\"0.750000\"/>\n" + 
				"			</observation>\n" + 
				"		</component>\n" + 
				"		<component>\n" + 
				"			<observation classCode=\"OBS\" moodCode=\"EVN\">\n" + 
				"				<!-- ACI Numerator Denominator Type Measure Numerator Data templateId -->\n" + 
				"				<templateId root=\"2.16.840.1.113883.10.20.27.3.31\" extension=\"2016-09-01\"/>\n" + 
				"				<qed resultName=\"aciNumeratorDenominator\" resultValue=\"600\">\n" +
				"					<templateId root=\"Q.E.D\"/>\n" +
				"				</qed>" +
				"			</observation>\n" + 
				"		</component>\n" + 
				"		<component>\n" + 
				"			<observation classCode=\"OBS\" moodCode=\"EVN\">\n" + 
				"				<!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->\n" + 
				"				<templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\"/>\n" + 
				"				<qed resultName=\"aciNumeratorDenominator\" resultValue=\"800\">\n" +
				"					<templateId root=\"Q.E.D\"/>\n" +
				"				</qed>" +
				"			</observation>\n" + 
				"		</component>\n" + 
				"	</organizer>\n" + 
				"</entry>";
		
		Element dom =  XmlUtils.stringToDOM(xmlFragment);
		
		QppXmlDecoder decoder = new QppXmlDecoder();
		decoder.setDom(dom);

		Node root = decoder.decode();

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));

		// For all decoders this should be either a value or child node
		assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));
		// This is the child node that is produced by the intended decoder
		Node aciProportionMeasureNode = root.getChildNodes().get(0);
		// Should have a aggregate count node 
		assertThat("returned node should have two child decoder nodes", aciProportionMeasureNode.getChildNodes().size(), is(2));
		
		assertThat("measureId should be ACI-PEA-1",
				(String) aciProportionMeasureNode.getValue("measureId"), is("ACI-PEA-1"));
	
		List<String> testTemplateIds = new ArrayList<>();
		for (Node node : aciProportionMeasureNode.getChildNodes()) {
			testTemplateIds.add(node.getId());
		}
		
		assertThat("Should have Numerator", testTemplateIds.contains("2.16.840.1.113883.10.20.27.3.31"), is(true));
		assertThat("Should have Denominator", testTemplateIds.contains("2.16.840.1.113883.10.20.27.3.32"), is(true));

	}

}
