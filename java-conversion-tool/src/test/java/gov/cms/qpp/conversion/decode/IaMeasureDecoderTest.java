package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Test class for the IaMeasureDecoder
 */
public class IaMeasureDecoderTest {

	@Test
	public void internalDecode() throws Exception {
		String xmlFragment = getValidXmlFragment();
		IaMeasureDecoder decoder = new IaMeasureDecoder();
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));
		assertThat("Root node should be placeholder ", root.getId() , is("placeholder"));
		assertThat("The child nodes should not be null", root.getChildNodes(), notNullValue());
		assertThat("There should be only one child node", root.getChildNodes().size(), is(1));

		Node iaMeasure = root.getChildNodes().get(0);
		assertThat("IAMeasure node should be IA_MEASURE ", iaMeasure.getId(),
				is(TemplateId.IA_MEASURE.getTemplateId()));
		assertThat("The IAMeasure nodes children should not be null", iaMeasure.getChildNodes(), notNullValue());
		assertThat("There should be only one child node", iaMeasure.getChildNodes().size(), is(1));

		Node aciMeasurePerformed = iaMeasure.getChildNodes().get(0);
		assertThat("ACI Measure Performed node should be ACI_MEASURE_PERFORMED ", aciMeasurePerformed.getId(),
				is(TemplateId.ACI_MEASURE_PERFORMED.getTemplateId()));
		String value = aciMeasurePerformed.getValue("measurePerformed");
		assertThat("The ACI_MEASURE_PERFORMED value should be \"Y\"" , value, is("Y"));
	}

	@Test
	public void missingChildTest() throws Exception {
		String xmlFragment = getValidXmlFragment();
		xmlFragment = removeChildFragment(xmlFragment);
		IaMeasureDecoder decoder = new IaMeasureDecoder();
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));
		assertThat("Root node should be placeholder ", root.getId() , is("placeholder"));
		assertThat("The child nodes should not be null", root.getChildNodes(), notNullValue());
		assertThat("There should be only one child node", root.getChildNodes().size(), is(1));

		Node iaMeasure = root.getChildNodes().get(0);
		assertThat("IAMeasure node should be IA_MEASURE ", iaMeasure.getId(),
				is(TemplateId.IA_MEASURE.getTemplateId()));
		assertThat("The IAMeasure nodes children should not be null", iaMeasure.getChildNodes(), notNullValue());
		assertThat("There should not be any child node", iaMeasure.getChildNodes().size(), is(0));
	}

	@Test
	public void extraXMLTest() throws Exception {
		String xmlFragment = getValidXmlFragment();
		IaMeasureDecoder decoder = new IaMeasureDecoder();
		xmlFragment = addExtraXml(xmlFragment);
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));

		assertThat("Root node should be placeholder ", root.getId() , is("placeholder"));
		assertThat("The child nodes should not be null", root.getChildNodes(), notNullValue());
		assertThat("There should be only one child node", root.getChildNodes().size(), is(1));

		Node iaMeasure = root.getChildNodes().get(0);
		assertThat("IAMeasure node should be IA_MEASURE ", iaMeasure.getId(),
				is(TemplateId.IA_MEASURE.getTemplateId()));
		assertThat("The IAMeasure nodes children should not be null", iaMeasure.getChildNodes(), notNullValue());
		assertThat("There should be only one child node", iaMeasure.getChildNodes().size(), is(1));

		Node aciMeasurePerformed = iaMeasure.getChildNodes().get(0);
		assertThat("ACI Measure Performed node should be ACI_MEASURE_PERFORMED ", aciMeasurePerformed.getId(),
				is(TemplateId.ACI_MEASURE_PERFORMED.getTemplateId()));
		String value = aciMeasurePerformed.getValue("measurePerformed");
		assertThat("The ACI_MEASURE_PERFORMED value should be \"Y\"" , value, is("Y"));
	}

	private String addExtraXml(String source) {
		int start = source.indexOf("<observation");
		int end = source.indexOf(">", start) + 1;
		StringBuilder extraXml = new StringBuilder();
		extraXml.append(source.substring(0, end));
		extraXml.append(" SOME EXTRA STUFF GOES HERE <An unexpected=\"tag\"/> and yet more stuff\n");
		extraXml.append(source.substring(end));
		return extraXml.toString();
	}

	private String removeChildFragment(String source) {
		int start = source.indexOf("<component>");
		int end = source.indexOf("</component>") + 12;
		StringBuilder component = new StringBuilder();
		component.append(source.substring(0, start));
		component.append(source.substring(end));
		return component.toString();
	}

	private String getValidXmlFragment() {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
		+"<entry  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\" > \n"
		+ "<organizer classCode=\"CLUSTER\" moodCode=\"EVN\"> \n"
		+ "	<!-- Implied template Measure Reference templateId --> \n"
		+ "	<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/> \n"
		+ "	<!-- Improvement Activity Performed Reference and Results templateId --> \n"
		+ "	<templateId root=\"2.16.840.1.113883.10.20.27.3.33\" extension=\"2016-09-01\"/> \n"
		+ "	<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/> \n"
		+ "	<statusCode code=\"completed\"/> \n"
		+ "	<reference typeCode=\"REFR\"> \n"
		+ "		<!-- Reference to a particular ACI measure's unique identifier. --> \n"
		+ "		<externalDocument classCode=\"DOC\" moodCode=\"EVN\"> \n"
		+ "			<!-- This is a temporary root OID that indicates this is an improvement activity identifier --> \n"
		+ "			<!-- extension is the unique identifier for an improvement activity. \"IA_EPA_1\" is for illustration only. --> \n"
		+ "			<id root=\"2.16.840.1.113883.3.7034\" extension=\"IA_EPA_1\"/> \n"
		+ "			<!-- Improvement activity narrative text (for reference) --> \n"
		+ "			<text> Collection of patient experience and satisfaction data on access to care and development of an improvement plan, such \n"
		+ "			as outlining steps for improving communications with patients to help understanding of urgent access needs.</text> \n"
		+ "		</externalDocument> \n"
		+ "	</reference> \n"
		+ "	<component> \n"
		+ "		<observation classCode=\"OBS\" moodCode=\"EVN\"> \n"
		+ "			<!-- Measure Performed templateId --> \n"
		+ "			<templateId root=\"2.16.840.1.113883.10.20.27.3.27\" extension=\"2016-09-01\"/> \n"
		+ "			<code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/> \n"
		+ "			<statusCode code=\"completed\"/> \n"
		+ "			<value xsi:type=\"CD\" code=\"Y\" displayName=\"xxx\" codeSystemName=\"Yes/no indicator (HL7 Table 0136)\" codeSystem=\"2.16.840.1.113883.12.136\"/> \n"
		+ "		</observation> \n"
		+ "	</component> \n"
		+ "</organizer> \n"
		+ "</entry>";
		return xmlFragment;
	}

}