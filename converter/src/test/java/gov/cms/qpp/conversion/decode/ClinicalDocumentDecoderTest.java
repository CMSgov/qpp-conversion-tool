package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.reflections.util.ClasspathHelper;

import static com.google.common.truth.Truth.assertWithMessage;

class ClinicalDocumentDecoderTest {

	private static final String ENTITY_ID_VALUE = "AR000000";
	private static String xmlFragment;
	private Node clinicalDocument;

	@BeforeAll
	static void init() throws IOException {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III-abridged.xml");
		xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());
	}

	@BeforeEach
	void setupTest() throws XmlException {
		Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		clinicalDocument = root.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(clinicalDocument.getChildNodes());
	}

	@Test
	void testRootId() {
		assertWithMessage("Must have be the correct TemplateId")
				.that(clinicalDocument.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.CLINICAL_DOCUMENT);
	}

	@Test
	void testRootProgramName() {
		assertWithMessage("Must be the correct Program Name")
				.that(clinicalDocument.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
	}

	@Test
	void testRootNationalProviderIdentifier() {
		assertWithMessage("Must have the correct NPI")
				.that(clinicalDocument.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
	}

	@Test
	void testRootTaxpayerIdentificationNumber() {
		assertWithMessage("Must have the correct TIN")
				.that(clinicalDocument.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void testAciCategory() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertWithMessage("returned category should be aci")
				.that(aciSectionNode.getValue("category"))
				.isEqualTo("aci");
	}

	@Test
	void testAciPea1MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertWithMessage("returned measureId ACI-PEA-1")
				.that(aciSectionNode.getChildNodes().get(0).getValue("measureId"))
				.isEqualTo("ACI-PEA-1");
	}

	@Test
	void testAciEp1MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertWithMessage("returned measureId ACI_EP_1")
				.that(aciSectionNode.getChildNodes().get(1).getValue("measureId"))
				.isEqualTo("ACI_EP_1");
	}

	@Test
	void testAciCctpe3MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertWithMessage("returned measureId ACI_CCTPE_3")
				.that(aciSectionNode.getChildNodes().get(2).getValue("measureId"))
				.isEqualTo("ACI_CCTPE_3");
	}

	@Test
	void testIaCategory() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		assertWithMessage("returned category")
				.that(iaSectionNode.getValue("category"))
				.isEqualTo("ia");
	}

	@Test
	void testIaMeasureId() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertWithMessage("returned should have measureId")
				.that(iaMeasureNode.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
	}

	@Test
	void testClinicalDocumentIgnoresGarbage() throws IOException, XmlException {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("QRDA-III-with-extra-elements.xml");
		String xmlWithGarbage = IOUtils.toString(stream, Charset.defaultCharset());

		Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlWithGarbage));
		clinicalDocument = root.findFirstNode(TemplateId.CLINICAL_DOCUMENT);

		assertWithMessage("Should contain a program name")
				.that(clinicalDocument.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);

		assertWithMessage("Should contain a TIN")
				.that(clinicalDocument.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void testIaMeasurePerformed() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		Node iaMeasurePerformedNode = iaMeasureNode.getChildNodes().get(0);
		assertWithMessage("returned measurePerformed")
				.that(iaMeasurePerformedNode.getValue("measurePerformed"))
				.isEqualTo("Y");
	}

	@Test
	void decodeClinicalDocumentInternalDecode() throws Exception {
		Element clinicalDocument = makeClinicalDocument("MIPS");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo(ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
		assertWithMessage("Clinical Document doesn't contain the ACI Section child node")
				.that(testChildNode)
				.isNotNull();
		assertWithMessage("Clinical Document doesn't contain ACI Section category")
				.that(testChildNode.getValue("category"))
				.isEqualTo("aci");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeMIPSIndividual() throws Exception {
		Element clinicalDocument = makeClinicalDocument("MIPS_INDIV");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo(ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
		assertWithMessage("Clinical Document doesn't contain the ACI Section child node")
				.that(testChildNode)
				.isNotNull();
		assertWithMessage("Clinical Document doesn't contain ACI Section category")
				.that(testChildNode.getValue("category"))
				.isEqualTo("aci");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeMIPSGroup() throws Exception {
		Element clinicalDocument = makeClinicalDocument("MIPS_GROUP");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo(ClinicalDocumentDecoder.ENTITY_GROUP);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
		assertWithMessage("Clinical Document doesn't contain the ACI Section child node")
				.that(testChildNode)
				.isNotNull();
		assertWithMessage("Clinical Document doesn't contain ACI Section category")
				.that(testChildNode.getValue("category"))
				.isEqualTo("aci");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeCPCPlus() throws Exception {
		Element clinicalDocument = makeClinicalDocument(ClinicalDocumentDecoder.CPCPLUS);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo(ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
		assertWithMessage("Clinical Document doesn't contain the ACI Section child node")
				.that(testChildNode)
				.isNotNull();
		assertWithMessage("Clinical Document doesn't contain ACI Section category")
				.that(testChildNode.getValue("category"))
				.isEqualTo("aci");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeUnknown() throws Exception {
		Element clinicalDocument = makeClinicalDocument("Unknown");
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		Node testChildNode = testParentNode.getChildNodes().get(0);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo("unknown");
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo("individual");
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
		assertWithMessage("Clinical Document doesn't contain the ACI Section child node")
				.that(testChildNode)
				.isNotNull();
		assertWithMessage("Clinical Document doesn't contain ACI Section category")
				.that(testChildNode.getValue("category"))
				.isEqualTo("aci");
	}

	@Test
	void decodeCpcPlusEntityIdTest() throws Exception {
		Element clinicalDocument = makeClinicalDocument(ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		clinicalDocument.addContent( prepareParticipant( clinicalDocument.getNamespace()) );
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		assertWithMessage("Clinical Document contains the Entity Id")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_ID))
				.isEqualTo(ENTITY_ID_VALUE);
	}

	@Test
	void decodeCpcPracticeSiteAddressTest() throws Exception {
		Element clinicalDocument = makeClinicalDocument(ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		clinicalDocument.addContent( prepareParticipant( clinicalDocument.getNamespace()) );
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, testParentNode);
		assertWithMessage("Clinical Document contains the Entity Id")
				.that(testParentNode.getValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR))
				.isEqualTo("testing123");
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

	// This is the Entity Id for CPCPlus program name
	private Element prepareParticipant(Namespace rootns) {
		Element participant = new Element("participant", rootns);
		Element associatedEntity = new Element("associatedEntity", rootns);

		Element entityId = new Element("id", rootns)
			.setAttribute("root", "2.16.840.1.113883.3.249.5.1")
			.setAttribute("extension", ENTITY_ID_VALUE)
			.setAttribute("assigningAuthorityName", "CMS-CMMI");
		Element addr = new Element("addr", rootns)
			.setText("testing123");
		associatedEntity.addContent(entityId);
		associatedEntity.addContent(addr);
		participant.addContent(associatedEntity);
		return participant;
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