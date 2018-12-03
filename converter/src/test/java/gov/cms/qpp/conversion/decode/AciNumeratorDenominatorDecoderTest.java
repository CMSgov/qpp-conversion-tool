package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class AciNumeratorDenominatorDecoderTest {

	private static final String MEASURE_ID = "ACI-PEA-1";

	@Test
	void decodeAggregateCountAsNode() throws Exception {
		String xmlFragment = XmlUtils.buildString(
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
				"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"    <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
				"    <statusCode code=\"completed\"/>", "    <value xsi:type=\"INT\" value=\"600\"/>",
				"    <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
				"  </observation>",
				"</root>");

		Node aggregateCountNode = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		assertThat(aggregateCountNode.getChildNodes().get(0).getValue("aggregateCount"))
				.isEqualTo("600");
	}

	@Test
	void decodeAciNumeratorDenominatorNullValueAsNode() throws Exception {
		String xmlFragment = XmlUtils.buildString(
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\" >",
				"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"    <value xsi:type=\"INT\"/>",
				"  </observation>",
				"</root>");

		Node numDenomNode = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		assertWithMessage("aci numerator/denominator value should be null")
				.that(numDenomNode.getChildNodes().get(0).getValue("aggregateCount")).isNull();
	}

	@Test
	void decodeAciNumeratorDenominatorNullElementAsNode() throws Exception {
		String xmlFragment = XmlUtils.buildString(
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
				"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"  </observation>",
				"</root>");

		Node numDenomNode = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		assertWithMessage("aci numerator/denominator value should be null")
				.that(numDenomNode.getChildNodes().get(0).getValue("aggregateCount")).isNull();
	}

	@Test
	void decodeValidAciNumeratorDenominatorTest() throws XmlException {
		Node aciMeasureNode = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(getValidXmlFragment()));
		Node numeratorDenominatorNode = aciMeasureNode.getChildNodes().get(0);
		int numberNodes = countNodes(aciMeasureNode);
		List<Node> nodeList = aciMeasureNode.findNode(TemplateId.ACI_NUMERATOR);

		assertThat(numeratorDenominatorNode.getValue("measureId"))
				.isEqualTo(MEASURE_ID);

		assertThat(numeratorDenominatorNode.getChildNodes().get(0).getType())
				.isEqualTo(TemplateId.ACI_NUMERATOR);

		assertThat(numeratorDenominatorNode.getChildNodes().get(1).getType())
				.isEqualTo(TemplateId.ACI_DENOMINATOR);

		nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT);

		assertWithMessage("Decoded xml fragment %s should contain %s",
				TemplateId.ACI_NUMERATOR.name(), TemplateId.ACI_AGGREGATE_COUNT.name())
				.that(nodeList).hasSize(1);

		nodeList = aciMeasureNode.findNode(TemplateId.ACI_DENOMINATOR);
		assertWithMessage("Decoded xml fragment should contain %s", TemplateId.ACI_DENOMINATOR.name())
				.that(nodeList).hasSize(1);

		nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT);
		assertWithMessage("Decoded xml fragment %s should contain %s",
				TemplateId.ACI_NUMERATOR.name(), TemplateId.ACI_AGGREGATE_COUNT.name())
				.that(nodeList).hasSize(1);

		assertWithMessage("Decoded xml fragment %s should contain 6 nodes", aciMeasureNode.getType().name())
				.that(numberNodes).isEqualTo(6);

		assertWithMessage("measureId should be %s", MEASURE_ID)
				.that(numeratorDenominatorNode.getValue("measureId"))
				.isEqualTo(MEASURE_ID);
	}

	@Test
	void decodeAciNumeratorDenominatorExtraneousXMLTest() throws XmlException {
		String xmlFragment = getValidXmlFragment();
		xmlFragment = xmlFragment.replaceAll("<statusCode ",
				"\n<Stuff arbitrary=\"123\"><newnode>Some extra stuff</newnode></Stuff>Unexpected stuff appears here\n\n<statusCode ");

		Node aciMeasureNode = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		assertWithMessage("Decoded xml fragment should contain one child node")
				.that(aciMeasureNode.getChildNodes())
				.hasSize(1);

		List<Node> nodeList = aciMeasureNode.findNode(TemplateId.ACI_NUMERATOR);
		assertWithMessage("Decoded xml fragment should contain %s", TemplateId.ACI_NUMERATOR.name())
				.that(nodeList).hasSize(1);

		nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT);
		assertWithMessage("Decoded xml fragment %s should contain %s",
				TemplateId.ACI_NUMERATOR.name(), TemplateId.ACI_AGGREGATE_COUNT.name())
				.that(nodeList).hasSize(1);

		nodeList = aciMeasureNode.findNode(TemplateId.ACI_DENOMINATOR);
		assertWithMessage("Decoded xml fragment should contain %s", TemplateId.ACI_DENOMINATOR.name())
				.that(nodeList).hasSize(1);

		nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT);
		assertWithMessage("Decoded xml fragment %s should contain %s",
				TemplateId.ACI_NUMERATOR.name(), TemplateId.ACI_AGGREGATE_COUNT.name())
				.that(nodeList).hasSize(1);
		int numberNodes = countNodes(aciMeasureNode);
		assertWithMessage("Decoded xml fragment %s should contain 6 nodes", aciMeasureNode.getType().name())
				.that(numberNodes).isEqualTo(6);
	}

	@Test
	void testInternalDecode() {
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

		Node thisNode = new Node();

		AciNumeratorDenominatorDecoder objectUnderTest = new AciNumeratorDenominatorDecoder(new Context());
		objectUnderTest.setNamespace(element.getNamespace());

		//execute
		objectUnderTest.decode(element, thisNode);

		//assert
		assertThat(thisNode.getValue("measureId"))
				.isEqualTo(MEASURE_ID);
	}

	private int countNodes(Node parent) {

		if (parent == null) {
			return 0;
		}
		List<Node> children = parent.getChildNodes();
		if (children == null || children.isEmpty()) {
			return 1;
		}
		int count = 1;
		for (Node node : children) {
			count += countNodes(node);
		}
		return count;
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
