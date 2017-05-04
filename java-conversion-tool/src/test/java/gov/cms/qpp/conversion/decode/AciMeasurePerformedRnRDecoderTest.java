package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class AciMeasurePerformedRnRDecoderTest {
	private static final String MEASURE_ID = "ACI_INFBLO_1";

	private AciMeasurePerformedRnRDecoder objectUnderTest = new AciMeasurePerformedRnRDecoder();

	@Test
	public void internalDecodeReturnsTreeContinue() {
		//set-up
		Namespace rootns = Namespace.getNamespace("urn:hl7-org:v3");
		Namespace ns = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element element = new Element("organizer", rootns);
		Element templateIdElement = new Element("templateId", rootns)
			                            .setAttribute("root","2.16.840.1.113883.10.20.27.3.28");
		Element referenceElement = new Element("reference", rootns);
		Element externalDocumentElement = new Element("externalDocument", rootns);
		Element idElement = new Element("id", rootns).setAttribute("extension", MEASURE_ID);

		externalDocumentElement.addContent(idElement);
		referenceElement.addContent(externalDocumentElement);
		element.addContent(templateIdElement);
		element.addContent(referenceElement);
		element.addNamespaceDeclaration(ns);

		Node aciMeasurePerformedNode = new Node();

		objectUnderTest.setNamespace(element, objectUnderTest);

		//execute
		DecodeResult decodeResult = objectUnderTest.internalDecode(element, aciMeasurePerformedNode);

		//assert
		assertThat("The decode result is incorrect.", decodeResult, is(DecodeResult.TREE_CONTINUE));
		String actualMeasureId = aciMeasurePerformedNode.getValue("measureId");
		assertThat("measureId must not be null.", actualMeasureId, is(not(nullValue())));
		assertThat("measureId is incorrect.", actualMeasureId, is(MEASURE_ID));
	}

	@Test
	public void testUpperLevel() throws XmlException {
		Node wrapperNode = new QppXmlDecoder().decode(XmlUtils.stringToDom(getValidXmlFragment()));
		Node aciMeasurePerformedNode = wrapperNode.getChildNodes().get(0);

		String actualMeasureId = aciMeasurePerformedNode.getValue("measureId");

		assertThat("The measureId must not be null.", actualMeasureId, is(not(nullValue())));
		assertThat("The measureId is incorrect.", actualMeasureId, is(MEASURE_ID));
	}

	private String getValidXmlFragment() {
		return XmlUtils.buildString(
			"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
			"\n<entry>",
			"\n<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">",
			"\n<!-- Implied template Measure Reference templateId -->",
			"\n<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/>",
			"\n<!-- ACI Numerator Denominator Type Measure Reference and Results templateId -->",
			"\n<templateId root=\"2.16.840.1.113883.10.20.27.3.29\" extension=\"2016-09-01\"/>",
			"\n<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>",
			"\n<statusCode code=\"completed\"/>",
			"\n<reference typeCode=\"REFR\">",
			"\n <!-- Reference to a particular ACI measure's unique identifier. -->",
			"\n <externalDocument classCode=\"DOC\" moodCode=\"EVN\">",
			"\n     <!-- This is a temporary root OID that indicates this is an ACI measure identifier -->",
			"\n     <!-- extension is the unique identifier for an ACI measure. \"ACI-PEA-1\" is for illustration only. -->",
			"\n     <id root=\"2.16.840.1.113883.3.7031\" extension=\"" + MEASURE_ID + "\"/>",
			"\n     <!-- ACI measure title -->",
			"\n     <text>Patient Access</text>",
			"\n </externalDocument>",
			"\n</reference>",
			"\n<component>",
			"\n<observation classCode=\"OBS\" moodCode=\"EVN\">",
			"\n     <!-- Measure Performed templateId -->",
			"\n     <templateId root=\"2.16.840.1.113883.10.20.27.3.27\" extension=\"2016-09-01\"/>",
			"\n     <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>",
			"\n     <statusCode code=\"completed\"/>",
			"\n     <value xsi:type=\"CD\" code=\"Y\" displayName=\"Yes\" codeSystemName=\"Yes/no indicator (HL7 Table 0136)\" codeSystem=\"2.16.840.1.113883.12.136\"/>",
			"\n</observation>",
			"\n</component>",
			"\n</organizer>",
			"\n</entry>",
			"\n</root>\n");
	}
}