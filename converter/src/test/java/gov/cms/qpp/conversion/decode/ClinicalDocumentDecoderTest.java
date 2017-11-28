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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import static com.google.common.truth.Truth.assertThat;

class ClinicalDocumentDecoderTest {

	private static final String ENTITY_ID_VALUE = "AR000000";
	private static String xmlFragment;
	private static String xmlWithGarbage;

	@BeforeAll
	static void init() throws IOException {
		InputStream abridgedStream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III-abridged.xml");
		xmlFragment = IOUtils.toString(abridgedStream, Charset.defaultCharset());

		InputStream extraElementsStream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("QRDA-III-with-extra-elements.xml");
		xmlWithGarbage = IOUtils.toString(extraElementsStream, Charset.defaultCharset());
	}

	@Test
	void testRootIdSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);

		assertThat(clinicalDocument.getType()).isEquivalentAccordingToCompareTo(TemplateId.CLINICAL_DOCUMENT);
	}

	@Test
	void testRootProgramNameSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);

		assertThat(clinicalDocument.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
	}

	@Test
	void testEntityTypeIndividualSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);

		assertThat(clinicalDocument.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo(ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);
	}

	@Test
	void testRootNationalProviderIdentifierSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);

		assertThat(clinicalDocument.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
	}

	@Test
	void testRootTaxpayerIdentificationNumberSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);

		assertThat(clinicalDocument.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void testAciCategorySuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);

		assertThat(aciSectionNode.getValue("category"))
				.isEqualTo("aci");
	}

	@Test
	void testAciPea1MeasureIdSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);

		assertThat(aciSectionNode.getChildNodes().get(0).getValue("measureId"))
				.isEqualTo("ACI-PEA-1");
	}

	@Test
	void testAciEp1MeasureIdSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);

		assertThat(aciSectionNode.getChildNodes().get(1).getValue("measureId"))
				.isEqualTo("ACI_EP_1");
	}

	@Test
	void testAciCctpe3MeasureIdSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);

		assertThat(aciSectionNode.getChildNodes().get(2).getValue("measureId"))
				.isEqualTo("ACI_CCTPE_3");
	}

	@Test
	void testIaCategorySuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlFragment);

		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		assertThat(iaSectionNode.getValue("category"))
				.isEqualTo("ia");
	}

	@Test
	void testIaMeasureIdSuccess() throws XmlException{
		Node clinicalDocument = decodeXmlFragment(xmlFragment);

		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertThat(iaMeasureNode.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
	}

	@Test
	void testXmlWithGarbageProgramNameDecodeSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlWithGarbage);

		assertThat(clinicalDocument.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
	}

	@Test
	void testXmlWithGarbageTinDecodeSuccess() throws XmlException {
		Node clinicalDocument = decodeXmlFragment(xmlWithGarbage);

		assertThat(clinicalDocument.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void testInternalDecodeMipsIndividualReturnsCorrectProgramName() throws Exception {
		Node testParentNode = internalDecodeWithProgram("MIPS_INDIV");

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
	}

	@Test
	void testInternalDecodeMipsIndividualReturnsCorrectEntity() throws Exception {
		Node testParentNode = internalDecodeWithProgram("MIPS_INDIV");

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo(ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);
	}

	@Test
	void testInternalDecodeMipsGroupReturnsCorrectProgramName() throws Exception {
		Node testParentNode = internalDecodeWithProgram("MIPS_GROUP");

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
	}

	@Test
	void testInternalDecodeMipsGroupReturnsCorrectEntity() throws Exception {
		Node testParentNode = internalDecodeWithProgram("MIPS_GROUP");

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo(ClinicalDocumentDecoder.ENTITY_GROUP);
	}

	@Test
	void testInternalDecodeCpcPlusReturnsCorrectProgramName() throws Exception {
		Node testParentNode = internalDecodeWithProgram(ClinicalDocumentDecoder.CPCPLUS);

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo(ClinicalDocumentDecoder.CPCPLUS.toLowerCase());
	}

	@Test
	void testInternalDecodeCpcPlusReturnsCorrectEntity() throws Exception {
		Node testParentNode = internalDecodeWithProgram(ClinicalDocumentDecoder.CPCPLUS);

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEmpty();
	}

	@Test
	void testInternalDecodeUnknownProgramNameReturnsUnknown() throws Exception {
		Node testParentNode = internalDecodeWithProgram("Unknown");

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME))
				.isEqualTo("unknown");
	}

	@Test
	void testInternalDecodeUnknownProgramNameReturnsIndividualEntity() throws Exception {
		Node testParentNode = internalDecodeWithProgram("Unknown");

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo("individual");
	}

	@Test
	void decodeCpcPlusEntityIdTest() throws Exception {
		Node testParentNode = internalDecodeWithProgram(ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.ENTITY_ID))
				.isEqualTo(ENTITY_ID_VALUE);
	}

	@Test
	void decodeCpcPracticeSiteAddressTest() throws Exception {
		Node testParentNode = internalDecodeWithProgram(ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);

		assertThat(testParentNode.getValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR))
				.isEqualTo("testing123");
	}

	private Node decodeXmlFragment(String xmlFragment) throws XmlException {
		Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		Node clinicalDocument = root.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(clinicalDocument.getChildNodes());

		return clinicalDocument;
	}

	private Node internalDecodeWithProgram(String program) {
		Element clinicalDocument = makeClinicalDocument(program);
		Node clinicalDocumentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, clinicalDocumentNode);

		return clinicalDocumentNode;
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
		if (ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME.equalsIgnoreCase(programName)) {
			clinicalDocument.addContent(prepareParticipant(clinicalDocument.getNamespace()));
		}

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