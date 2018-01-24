package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.common.truth.Truth.assertThat;

class AciMeasurePerformedRnRDecoderTest {
	private static final String MEASURE_ID = "ACI_INFBLO_1";

	@Test
	void internalDecodeReturnsTreeContinue() {
		//set-up
		AciMeasurePerformedRnRDecoder objectUnderTest = new AciMeasurePerformedRnRDecoder(new Context());
		
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
		assertThat(decodeResult)
				.isEqualTo(DecodeResult.TREE_CONTINUE);
		String actualMeasureId = aciMeasurePerformedNode.getValue("measureId");
		assertThat(actualMeasureId)
				.isEqualTo(MEASURE_ID);
	}

	@Test
	void testUpperLevel() throws XmlException, IOException {
		String needsFormattingXml = TestHelper.getFixture("AciMeasurePerformedIsolated.xml");
		String xml = String.format(needsFormattingXml, MEASURE_ID);
		Node wrapperNode = new QrdaXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xml));
		Node aciMeasurePerformedNode = wrapperNode.getChildNodes().get(0);

		String actualMeasureId = aciMeasurePerformedNode.getValue("measureId");

		assertThat(actualMeasureId).isEqualTo(MEASURE_ID);
		long measurePerformedCount = aciMeasurePerformedNode.getChildNodes(
			node -> node.getType() == TemplateId.MEASURE_PERFORMED).count();
		assertThat(measurePerformedCount)
				.isEqualTo(1L);
	}
}