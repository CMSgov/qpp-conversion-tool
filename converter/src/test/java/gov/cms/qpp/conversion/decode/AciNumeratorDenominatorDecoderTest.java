package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class AciNumeratorDenominatorDecoderTest {

	private static final String MEASURE_ID = "ACI-PEA-1";

	@Test
	public void testDecodeAciNumeratorDenominatorContainsMeasureID() throws XmlException {
		Node aciMeasureNode = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(getValidXmlFragment()));
		Node numeratorDenominatorNode = aciMeasureNode.getChildNodes().get(0);

		assertThat(numeratorDenominatorNode.getValue("measureId")).isEqualTo(MEASURE_ID);
	}

	@Test
	public void testDecodeAciNumeratorDenominatorContainsANumerator() throws XmlException {
		Node aciMeasureNode = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(getValidXmlFragment()));
		Node numeratorDenominatorNode = aciMeasureNode.getChildNodes().get(0);

		assertThat(numeratorDenominatorNode.getChildNodes().get(0).getType()).isEqualTo(TemplateId.ACI_NUMERATOR);
	}

	@Test
	public void testDecodeAciNumeratorDenominatorContainsADenominator() throws XmlException {
		Node aciMeasureNode = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(getValidXmlFragment()));
		Node numeratorDenominatorNode = aciMeasureNode.getChildNodes().get(0);

		assertThat(numeratorDenominatorNode.getChildNodes().get(1).getType()).isEqualTo(TemplateId.ACI_DENOMINATOR);
	}

	@Test
	public void decodeAciNumeratorDenominatorExtraneousXMLTest() throws XmlException {
		String xmlFragment = getValidXmlFragment();
		xmlFragment = xmlFragment.replaceAll("<statusCode ",
				"\n<Stuff arbitrary=\"123\"><newnode>Some extra stuff</newnode></Stuff>Unexpected stuff appears here\n\n<statusCode ");

		Node aciMeasureNode = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		assertWithMessage("Decoded xml fragment should contain one child node")
				.that(aciMeasureNode.getChildNodes())
				.hasSize(1);
	}

	@Test
	public void decodeAciNumeratorDenominatorNullValueAsNode() throws Exception {
		String xmlFragment = XmlUtils.buildString(
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\" >",
				"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"    <value xsi:type=\"INT\"/>",
				"  </observation>",
				"</root>");

		Node numDenomNode = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		assertWithMessage("aci numerator/denominator value should be null")
				.that(numDenomNode.getChildNodes().get(0).getValue("aggregateCount")).isNull();
	}

	@Test
	public void decodeAciNumeratorDenominatorNullElementAsNode() throws Exception {
		String xmlFragment = XmlUtils.buildString(
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
				"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"  </observation>",
				"</root>");

		Node numDenomNode = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		assertWithMessage("aci numerator/denominator value should be null")
				.that(numDenomNode.getChildNodes().get(0).getValue("aggregateCount")).isNull();
	}

	private String getValidXmlFragment() {
		return XmlUtils.buildString(
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
				"\n<entry>",
				"\n<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">",
				"\n<!-- Implied template Measure Reference templateId -->",
				"\n<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/>",
				"\n<!-- ACI Numerator Denominator Type Measure Reference and Results templateId -->",
				"\n<templateId root=\"2.16.840.1.113883.10.20.27.3.28\" extension=\"2017-06-01\"/>",
				"\n<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>",
				"\n<statusCode code=\"completed\"/>",
				"\n<reference typeCode=\"REFR\">",
				"\n <!-- Reference to a particular ACI measure's unique identifier. -->",
				"\n <externalDocument classCode=\"DOC\" moodCode=\"EVN\">",
				"\n     <!-- This is a temporary root OID that indicates this is an ACI measure identifier -->",
				"\n     <!-- extension is the unique identifier for an ACI measure. \"ACI-PEA-1\" is for illustration only. -->",
				"\n     <id root=\"2.16.840.1.113883.3.7031\" extension=\"ACI-PEA-1\"/>",
				"\n     <!-- ACI measure title -->",
				"\n     <text>Patient Access</text>",
				"\n </externalDocument>",
				"\n</reference>",
				"\n<component>",
				"\n <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"\n     <!-- ACI Numerator Denominator Type Measure Numerator Data templateId -->",
				"\n     <templateId root=\"2.16.840.1.113883.10.20.27.3.31\" extension=\"2016-09-01\"/>",
				"\n     <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>",
				"\n     <statusCode code=\"completed\"/>",
				"\n     <value xsi:type=\"CD\" code=\"NUMER\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\"/>",
				"\n     <!-- Numerator Count-->",
				"\n     <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">",
				"\n         <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"\n             <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"\n             <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
				"\n             <statusCode code=\"completed\"/>",
				"\n             <value xsi:type=\"INT\" value=\"600\"/>",
				"\n             <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
				"\n         </observation>",
				"\n     </entryRelationship>",
				"\n </observation>",
				"\n</component>",
				"\n<component>",
				"\n <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"\n     <!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->",
				"\n     <templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\"/>",
				"\n     <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>",
				"\n     <statusCode code=\"completed\"/>",
				"\n     <value xsi:type=\"CD\" code=\"DENOM\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\"/>",
				"\n     <!-- Denominator Count-->",
				"\n     <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">",
				"\n         <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"\n             <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"\n             <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
				"\n             <statusCode code=\"completed\"/>",
				"\n             <value xsi:type=\"INT\" value=\"800\"/>",
				"\n             <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
				"\n         </observation>",
				"\n     </entryRelationship>",
				"\n </observation>",
				"\n</component>",
				"\n</organizer>",
				"\n</entry>",
				"\n</root>\n");
	}
}
