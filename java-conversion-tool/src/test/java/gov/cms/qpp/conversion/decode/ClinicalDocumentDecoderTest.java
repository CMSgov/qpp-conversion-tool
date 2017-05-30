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
import org.reflections.util.ClasspathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentDecoderTest {

	private static String xmlFragment;
	private Node clinicalDocument;

	@BeforeClass
	public static void init() throws IOException {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III-abridged.xml");
		xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());
	}

	@Before
	public void setupTest() throws XmlException {
		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));
		clinicalDocument = root.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(clinicalDocument.getChildNodes());
	}

	@Test
	public void testRootId() {
		assertThat("template ID is correct", clinicalDocument.getType(), is(TemplateId.CLINICAL_DOCUMENT.getTemplateId()));
	}

	@Test
	public void testRootProgramName() {
		assertThat("programName is correct", clinicalDocument.getValue(ClinicalDocumentDecoder.PROGRAM_NAME),
			is(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME));
	}

	@Test
	public void testRootNationalProviderIdentifier() {
		assertThat("nationalProviderIdentifier correct",
			clinicalDocument.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is("2567891421"));
	}

	@Test
	public void testRootTaxpayerIdentificationNumber() {
		assertThat("taxpayerIdentificationNumber correct",
			clinicalDocument.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is("123456789"));
	}

	@Test
	public void testReportParameterSource() {
		Node reportParameterSectionNode = clinicalDocument.getChildNodes().get(0);
		assertThat("returned category", reportParameterSectionNode.getValue("source"), is("provider"));
	}

	@Test
	public void testReportActPerformanceStart() {
		Node reportParameterSectionNode = clinicalDocument.getChildNodes().get(0);
		Node reportingActSectionNodeMeasureNode = reportParameterSectionNode.getChildNodes().get(0);
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceStart"), is("20170101"));
	}

	@Test
	public void testReportActPerformanceEnd() {
		Node reportParameterSectionNode = clinicalDocument.getChildNodes().get(0);
		Node reportingActSectionNodeMeasureNode = reportParameterSectionNode.getChildNodes().get(0);
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceEnd"), is("20171231"));
	}

	@Test
	public void testAciCategory() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(1);
		assertThat("returned category should be aci", aciSectionNode.getValue("category"), is("aci"));
	}

	@Test
	public void testAciPea1MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(1);
		assertThat("returned measureId ACI-PEA-1", aciSectionNode.getChildNodes().get(0).getValue("measureId"), is("ACI-PEA-1"));
	}

	@Test
	public void testAciEp1MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(1);
		assertThat("returned measureId ACI_EP_1", aciSectionNode.getChildNodes().get(1).getValue("measureId"), is("ACI_EP_1"));
	}

	@Test
	public void testAciCctpe3MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(1);
		assertThat("returned measureId ACI_CCTPE_3", aciSectionNode.getChildNodes().get(2).getValue("measureId"), is("ACI_CCTPE_3"));
	}

	@Test
	public void testIaCategory() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(2);
		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));
	}

	@Test
	public void testIaMeasureId() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(2);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertThat("returned should have measureId", iaMeasureNode.getValue("measureId"), is("IA_EPA_1"));
	}

	@Test
	public void testClinicalDocumentIgnoresGarbage() throws IOException, XmlException {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("QRDA-III-with-extra-elements.xml");
		String xmlWithGarbage = IOUtils.toString(stream, Charset.defaultCharset());

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlWithGarbage));
		clinicalDocument = root.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		Node performanceYear = clinicalDocument.getChildNodes().get(0);

		assertThat("Should contain a program name",
			clinicalDocument.getValue(ClinicalDocumentDecoder.PROGRAM_NAME), is(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME));

		assertThat("Should contain a TIN",
			clinicalDocument.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER),is("123456789") );

		assertThat("Should contain a performance year end", performanceYear.getChildNodes().get(0).getValue("performanceEnd"),
				is("20171231"));

		assertThat("Should contain a performance year start", performanceYear.getChildNodes().get(0).getValue("performanceStart"),
				is("20170101"));
	}

	@Test
	public void testIaMeasurePerformed() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(2);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		Node iaMeasurePerformedNode = iaMeasureNode.getChildNodes().get(0);
		assertThat("returned measurePerformed", iaMeasurePerformedNode.getValue("measurePerformed"), is("Y"));
	}

	@Test
	public void decodeClinicalDocumentInternalDecode() throws Exception {

		Element clinicalDocument = makeClinicalDocument("MIPS");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder();
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertThat("Clinical Document doesn't contain program name",
			testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME), is(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME));
		assertThat("Clinical Document doesn't contain entity type",
			testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE), is(ClinicalDocumentDecoder.ENTITY_INDIVIDUAL));
		assertThat("Clinical Document doesn't contain national provider",
			testParentNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is("2567891421"));
		assertThat("Clinical Document doesn't contain taxpayer id number",
			testParentNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is("123456789"));
		assertThat("Clinical Document doesn't contain the ACI Section child node", testChildNode, is(notNullValue()));
		assertThat("Clinical Document doesn't contain ACI Section category", testChildNode.getValue("category"), is("aci"));

	}

	@Test
	public void decodeClinicalDocumentInternalDecodeMIPSIndividual() throws Exception {

		Element clinicalDocument = makeClinicalDocument("MIPS_INDIV");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder();
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertThat("Clinical Document doesn't contain program name",
			testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME), is(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME));
		assertThat("Clinical Document doesn't contain entity type",
			testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE), is(ClinicalDocumentDecoder.ENTITY_INDIVIDUAL));
		assertThat("Clinical Document doesn't contain national provider",
			testParentNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is("2567891421"));
		assertThat("Clinical Document doesn't contain taxpayer id number",
			testParentNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is("123456789"));
		assertThat("Clinical Document doesn't contain the ACI Section child node", testChildNode, is(notNullValue()));
		assertThat("Clinical Document doesn't contain ACI Section category", testChildNode.getValue("category"), is("aci"));

	}

	@Test
	public void decodeClinicalDocumentInternalDecodeMIPSGroup() throws Exception {

		Element clinicalDocument = makeClinicalDocument("MIPS_GROUP");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder();
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertThat("Clinical Document doesn't contain program name",
			testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME), is(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME));
		assertThat("Clinical Document doesn't contain entity type",
			testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE), is(ClinicalDocumentDecoder.ENTITY_GROUP));
		assertThat("Clinical Document doesn't contain national provider",
			testParentNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is("2567891421"));
		assertThat("Clinical Document doesn't contain taxpayer id number",
			testParentNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is("123456789"));
		assertThat("Clinical Document doesn't contain the ACI Section child node", testChildNode, is(notNullValue()));
		assertThat("Clinical Document doesn't contain ACI Section category", testChildNode.getValue("category"), is("aci"));

	}
	@Test
	public void decodeClinicalDocumentInternalDecodeCPCPlus() throws Exception {

		Element clinicalDocument = makeClinicalDocument(ClinicalDocumentDecoder.CPCPLUS);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder();
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertThat("Clinical Document doesn't contain program name",
			testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME), is(ClinicalDocumentDecoder.CPCPLUS.toLowerCase()));
		assertThat("Clinical Document doesn't contain entity type",
			testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE), is(""));
		assertThat("Clinical Document doesn't contain national provider",
			testParentNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is("2567891421"));
		assertThat("Clinical Document doesn't contain taxpayer id number",
			testParentNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is("123456789"));
		assertThat("Clinical Document doesn't contain the ACI Section child node", testChildNode, is(notNullValue()));
		assertThat("Clinical Document doesn't contain ACI Section category", testChildNode.getValue("category"), is("aci"));

	}

	@Test
	public void decodeClinicalDocumentInternalDecodeUnknown() throws Exception {

		Element clinicalDocument = makeClinicalDocument("Unknown");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder();
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertThat("Clinical Document doesn't contain program name",
			testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME), is("unknown"));
		assertThat("Clinical Document doesn't contain entity type",
			testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE), is("individual"));
		assertThat("Clinical Document doesn't contain national provider",
			testParentNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is("2567891421"));
		assertThat("Clinical Document doesn't contain taxpayer id number",
			testParentNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is("123456789"));
		assertThat("Clinical Document doesn't contain the ACI Section child node", testChildNode, is(notNullValue()));
		assertThat("Clinical Document doesn't contain ACI Section category", testChildNode.getValue("category"), is("aci"));

	}

	private Element makeClinicalDocument(String programName) {
		Namespace rootns = Namespace.getNamespace("urn:hl7-org:v3");
		Namespace ns = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element clinicalDocument = new Element("ClinicalDocument", rootns);
		clinicalDocument.addNamespaceDeclaration(ns);
		Element informationRecipient = prepareInfoRecipient(rootns, programName);
		Element documentationOf = prepareDocumentationElement(rootns);
		Element component = prepareComponentElement(rootns);

		clinicalDocument.addContent(informationRecipient);
		clinicalDocument.addContent(documentationOf);
		clinicalDocument.addContent(component);
		return clinicalDocument;
	}

	private Element prepareInfoRecipient(Namespace rootns, String programName) {
		Element informationRecipient = new Element("informationRecipient", rootns);
		Element intendedRecipient = new Element("intendedRecipient", rootns);
		Element programNameEl = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.3.249.7")
				.setAttribute("extension", programName);
		intendedRecipient.addContent(programNameEl);
		informationRecipient.addContent(intendedRecipient);
		return informationRecipient;
	}

	private Element prepareDocumentationElement(Namespace rootns) {
		Element documentationOf = new Element("documentationOf", rootns);
		Element serviceEvent = new Element("serviceEvent", rootns);
		Element performer = new Element("performer", rootns);
		Element assignedEntity = new Element("assignedEntity", rootns);
		Element nationalProviderIdentifier = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.4.6")
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
				.setAttribute("root", "2.16.840.1.113883.4.2")
				.setAttribute("extension", "123456789");

		representedOrganization.addContent(taxpayerIdentificationNumber);
		return representedOrganization;
	}

	private Element prepareComponentElement(Namespace rootns) {
		Element component = new Element("component", rootns);
		Element structuredBody = new Element("structuredBody", rootns);
		Element componentTwo = new Element("component", rootns);
		Element aciSectionElement = new Element("templateId", rootns);
		aciSectionElement.setAttribute("root", TemplateId.ACI_SECTION.getRoot());
		aciSectionElement.setAttribute("extension", TemplateId.ACI_SECTION.getExtension());

		componentTwo.addContent(aciSectionElement);
		structuredBody.addContent(componentTwo);
		component.addContent(structuredBody);
		return component;
	}
}