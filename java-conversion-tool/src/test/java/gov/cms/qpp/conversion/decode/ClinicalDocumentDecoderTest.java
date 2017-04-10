package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentDecoderTest {

	private static String xmlFragment;
	private Node root;

	@BeforeClass
	public static void init() throws IOException {
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III-abridged.xml");
		xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
	}

	@Before
	public void setupTest() throws XmlException {
		root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(root.getChildNodes());
	}

	@Test
	public void testRootId() {
		assertThat("template ID is correct", root.getId(), is(TemplateId.CLINICAL_DOCUMENT.getTemplateId()));
	}

	@Test
	public void testRootProgramName() {
		assertThat("programName is correct", root.getValue("programName"), is("mips"));
	}

	@Test
	public void testRootNationalProviderIdentifier() {
		assertThat("nationalProviderIdentifier correct", root.getValue("nationalProviderIdentifier"), is("2567891421"));
	}

	@Test
	public void testRootTaxpayerIdentificationNumber() {
		assertThat("taxpayerIdentificationNumber correct", root.getValue("taxpayerIdentificationNumber"), is("123456789"));
	}

	@Test
	public void testReportParameterSource() {
		Node reportParameterSectionNode = root.getChildNodes().get(0);
		assertThat("returned category", reportParameterSectionNode.getValue("source"), is("provider"));
	}

	@Test
	public void testReportActPerformanceStart() {
		Node reportParameterSectionNode = root.getChildNodes().get(0);
		Node reportingActSectionNodeMeasureNode = reportParameterSectionNode.getChildNodes().get(0);
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceStart"), is("20170101"));
	}

	@Test
	public void testReportActPerformanceEnd() {
		Node reportParameterSectionNode = root.getChildNodes().get(0);
		Node reportingActSectionNodeMeasureNode = reportParameterSectionNode.getChildNodes().get(0);
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceEnd"), is("20171231"));
	}

	@Test
	public void testAciCategory() {
		Node aciSectionNode = root.getChildNodes().get(1);
		assertThat("returned category should be aci", aciSectionNode.getValue("category"), is("aci"));
	}

	@Test
	public void testAciPea1MeasureId() {
		Node aciSectionNode = root.getChildNodes().get(1);
		assertThat("returned measureId ACI-PEA-1", aciSectionNode.getChildNodes().get(0).getValue("measureId"), is("ACI-PEA-1"));
	}

	@Test
	public void testAciEp1MeasureId() {
		Node aciSectionNode = root.getChildNodes().get(1);
		assertThat("returned measureId ACI_EP_1", aciSectionNode.getChildNodes().get(1).getValue("measureId"), is("ACI_EP_1"));
	}

	@Test
	public void testAciCctpe3MeasureId() {
		Node aciSectionNode = root.getChildNodes().get(1);
		assertThat("returned measureId ACI_CCTPE_3", aciSectionNode.getChildNodes().get(2).getValue("measureId"), is("ACI_CCTPE_3"));
	}

	@Test
	public void testIaCategory() {
		Node iaSectionNode = root.getChildNodes().get(2);
		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));
	}

	@Test
	public void testIaMeasureId() {
		Node iaSectionNode = root.getChildNodes().get(2);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertThat("returned should have measureId", iaMeasureNode.getValue("measureId"), is("IA_EPA_1"));
	}

	@Test
	public void testIaMeasurePerformed() {
		Node iaSectionNode = root.getChildNodes().get(2);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		Node iaMeasurePerformedNode = iaMeasureNode.getChildNodes().get(0);
		assertThat("returned measurePerformed", iaMeasurePerformedNode.getValue("measurePerformed"), is("Y"));
	}

	@Test
	public void decodeClinicalDocumentInternalDecode() throws Exception {

		Namespace rootns = Namespace.getNamespace("urn:hl7-org:v3");
		Namespace ns = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element clinicalDocument = new Element("ClinicalDocument", rootns);
		clinicalDocument.addNamespaceDeclaration(ns);

		Element informationRecipient = prepareInfoRecipient(rootns);

		Element documentationOf = prepareDocumentationElement(rootns);

		Element component = prepareComponentElement(rootns);

		clinicalDocument.addContent(informationRecipient);
		clinicalDocument.addContent(documentationOf);
		clinicalDocument.addContent(component);

		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder();
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertThat("Clinical Document doesn't contain program name", testParentNode.getValue("programName"), is("mips"));
		assertThat("Clinical Document doesn't contain national provider", testParentNode.getValue("nationalProviderIdentifier"), is("2567891421"));
		assertThat("Clinical Document doesn't contain taxpayer id number", testParentNode.getValue("taxpayerIdentificationNumber"), is("123456789"));
		assertThat("Clinical Document doesn't contain the ACI Section child node", testChildNode, is(notNullValue()));
		assertThat("Clinical Document doesn't contain ACI Section category", testChildNode.getValue("category"), is("aci"));

	}

	private Element prepareInfoRecipient(Namespace rootns) {
		Element informationRecipient = new Element("informationRecipient", rootns);
		Element intendedRecipient = new Element("intendedRecipient", rootns);
		Element programName = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.3.249.7")
				.setAttribute("extension", "MIPS");
		intendedRecipient.addContent(programName);
		informationRecipient.addContent(intendedRecipient);
		return informationRecipient;
	}

	private Element prepareDocumentationElement(Namespace rootns) {
		Element documentationOf = new Element("documentationOf", rootns);
		Element serviceEvent = new Element("serviceEvent", rootns);
		Element performer = new Element("performer", rootns);
		Element assignedEntity = new Element("assignedEntity", rootns);
		Element nationalProviderIdentifier = new Element("id", rootns)
				.setAttribute("root", TemplateId.NATIONAL_PROVIDER.getTemplateId())
				.setAttribute("extension", "2567891421");

		Element representedOrganization = prepareRepOrgWithTaxPayerId(rootns);
		assignedEntity.addContent(representedOrganization);
		assignedEntity.addContent(nationalProviderIdentifier);
		performer.addContent(assignedEntity);
		serviceEvent.addContent(performer);
		documentationOf.addContent(serviceEvent);
		return documentationOf;
	}

	private Element prepareRepOrgWithTaxPayerId(Namespace rootns) {
		Element representedOrganization = new Element("representedOrganization", rootns);
		Element taxpayerIdentificationNumber = new Element("id", rootns)
				.setAttribute("root", TemplateId.REPRESENTED_ORG.getTemplateId())
				.setAttribute("extension", "123456789");

		representedOrganization.addContent(taxpayerIdentificationNumber);
		return representedOrganization;
	}

	private Element prepareComponentElement(Namespace rootns) {
		Element component = new Element("component", rootns);
		Element structuredBody = new Element("structuredBody", rootns);
		Element componentTwo = new Element("component", rootns);
		Element aciSectionElement = new Element("templateId", rootns);
		aciSectionElement.setAttribute("root", TemplateId.STRUCTURED_BODY.getTemplateId());

		componentTwo.addContent(aciSectionElement);
		structuredBody.addContent(componentTwo);
		component.addContent(structuredBody);
		return component;
	}
}