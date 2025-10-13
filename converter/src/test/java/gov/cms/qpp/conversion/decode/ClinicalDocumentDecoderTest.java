package gov.cms.qpp.conversion.decode;

import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.Constants.*;

class ClinicalDocumentDecoderTest {

	private static final String ENTITY_ID_VALUE = "AR000000";
	public static final String CPC_PRACTICE_ROOT = "2.16.840.1.113883.3.249.5.1";
	private static String xmlFragment;
	private Node clinicalDocument;
	private static final String TEST_TIN = "123456789";
	private static final String TEST_NPI = "2567891421";
	private static final String TEST_MVP_ID = "MVP01123";
	private static final String TEST_SUBGROUP_ID = "SG-00000001";

	@BeforeAll
	static void init() throws IOException {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III-abridged.xml");
		xmlFragment = IOUtils.toString(stream, StandardCharsets.UTF_8);
	}

	@BeforeEach
	void setupTest() throws XmlException {
		Node root = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		clinicalDocument = root.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		// remove default nodes (will fail if defaults change)
	}

	@Test
	void testRootId() {
		assertThat(clinicalDocument.getType())
				.isEqualTo(TemplateId.CLINICAL_DOCUMENT);
	}

	@Test
	void testRootProgramName() {
		assertThat(clinicalDocument.getValue(PROGRAM_NAME))
				.isEqualTo(MIPS_PROGRAM_NAME);
	}

	@Test
	void testRootNationalProviderIdentifier() {
		assertThat(clinicalDocument.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo(TEST_NPI);
	}

	@Test
	void testRootTaxpayerIdentificationNumber() {
		assertThat(clinicalDocument.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo(TEST_TIN);
	}

	@Test
	void testRootMvpId() {
		assertThat(clinicalDocument.getValue(MVP_ID))
			.isEqualTo(TEST_MVP_ID);
	}

	@Test
	void testAciCategory() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertThat(aciSectionNode.getValue("category"))
				.isEqualTo("pi");
	}

	@Test
	void testAciPea1MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertThat(aciSectionNode.getChildNodes().get(0).getValue("measureId"))
				.isEqualTo("PI-PEA-1");
	}

	@Test
	void testAciEp1MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertThat(aciSectionNode.getChildNodes().get(1).getValue("measureId"))
				.isEqualTo("PI_EP_1");
	}

	@Test
	void testAciCctpe3MeasureId() {
		Node aciSectionNode = clinicalDocument.getChildNodes().get(0);
		assertThat(aciSectionNode.getChildNodes().get(2).getValue("measureId"))
				.isEqualTo("PI_CCTPE_3");
	}

	@Test
	void testIaCategory() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		assertThat(iaSectionNode.getValue("category"))
				.isEqualTo("ia");
	}

	@Test
	void testIaMeasureId() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertThat(iaMeasureNode.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
	}

	@Test
	void testClinicalDocumentIgnoresGarbage() throws IOException, XmlException {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("QRDA-III-with-extra-elements.xml");
		String xmlWithGarbage = IOUtils.toString(stream, StandardCharsets.UTF_8);

		Node root = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlWithGarbage));
		clinicalDocument = root.findFirstNode(TemplateId.CLINICAL_DOCUMENT);

		assertThat(clinicalDocument.getValue(PROGRAM_NAME))
				.isEqualTo(MIPS_PROGRAM_NAME);

		assertThat(clinicalDocument.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void testIaMeasurePerformed() {
		Node iaSectionNode = clinicalDocument.getChildNodes().get(1);
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		Node iaMeasurePerformedNode = iaMeasureNode.getChildNodes().get(0);
		assertThat(iaMeasurePerformedNode.getValue("measurePerformed"))
				.isEqualTo("Y");
	}

	@Test
	void decodeClinicalDocumentInternalDecode() {
		Element clinicalDocument = makeClinicalDocument("MIPS", false, false);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(PROGRAM_NAME))
				.isEqualTo(MIPS_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo(ENTITY_INDIVIDUAL);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void decodeAppClinicalDocumentInternalDecode() {
		Element clinicalDocument = makeClinicalDocument("APP1", false, false);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(PROGRAM_NAME))
				.isEqualTo(APP_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo(ENTITY_INDIVIDUAL);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeMIPSIndividual() {
		Element clinicalDocument = makeClinicalDocument("MIPS_INDIV", false, false);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(PROGRAM_NAME))
				.isEqualTo(MIPS_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo(ENTITY_INDIVIDUAL);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeMIPSAPPIndividual() {
		Element clinicalDocument = makeClinicalDocument("MIPS_APP1_INDIV", false, false);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(PROGRAM_NAME))
				.isEqualTo(APP_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo(ENTITY_INDIVIDUAL);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeMIPSGroup() {
		Element clinicalDocument = makeClinicalDocument("MIPS_GROUP", false, false);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(PROGRAM_NAME))
				.isEqualTo(MIPS_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo(ENTITY_GROUP);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isNull();
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeMIPSAPPGroup() {
		Element clinicalDocument = makeClinicalDocument("MIPS_APP1_GROUP", false, false);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(PROGRAM_NAME))
				.isEqualTo(APP_PROGRAM_NAME);
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo(ENTITY_GROUP);
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isNull();
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void decodeClinicalDocumentInternalDecodeUnknown() {
		Element clinicalDocument = makeClinicalDocument("Unknown", false, false);
		Node testParentNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertWithMessage("Clinical Document doesn't contain program name")
				.that(testParentNode.getValue(PROGRAM_NAME))
				.isEqualTo("unknown");
		assertWithMessage("Clinical Document doesn't contain entity type")
				.that(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo("individual");
		assertWithMessage("Clinical Document doesn't contain national provider")
				.that(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
		assertWithMessage("Clinical Document doesn't contain taxpayer id number")
				.that(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
	}

	@Test
	void decodeMipsVirtualGroup() {
		Element clinicalDocument = makeClinicalDocument(MIPS_VIRTUAL_GROUP, false, false);
		Node testParentNode = new Node();

		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertThat(testParentNode.getValue(ENTITY_TYPE))
			.isEqualTo(ENTITY_VIRTUAL_GROUP);
		assertThat(testParentNode.getValue(ENTITY_ID))
			.isEqualTo("x12345");
	}

	@Test
	void decodeMipsApmTest() {
		Element clinicalDocument = makeClinicalDocument(MIPS_APM, false, false);
		clinicalDocument.addContent(prepareParticipant(clinicalDocument.getNamespace(), CPC_PRACTICE_ROOT, ENTITY_ID_VALUE));
		Node testParentNode = new Node();

		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertThat(testParentNode.getValue(ENTITY_TYPE))
			.isEqualTo(ENTITY_APM);
		assertThat(testParentNode.getValue(ENTITY_ID))
			.isEqualTo("TEST_APM");
	}

	@Test
	void decodeMipsAppApmTest() {
		Element clinicalDocument = makeClinicalDocument(APP_APM, false, false);
		Node testParentNode = new Node();

		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertThat(testParentNode.getValue(ENTITY_TYPE))
				.isEqualTo(ENTITY_APM);
		assertThat(testParentNode.getValue(ENTITY_ID))
				.isEqualTo("TEST_APM");
	}

	@Test
	void decodeMvpMipsAppApmTest() {
		Element clinicalDocument = makeClinicalDocument(MIPS_APM, false, true);
		Node testParentNode = new Node();

		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertThat(testParentNode.getValue(ENTITY_TYPE))
			.isEqualTo(ENTITY_APM);
		assertThat(testParentNode.getValue(ENTITY_ID))
			.isEqualTo("TEST_APM");
		assertThat(testParentNode.getValue(MVP_ID))
			.isEqualTo(TEST_MVP_ID);
	}

	@Test
	void decodeMvpMipsIndividualTest() {
		Element clinicalDocument = makeClinicalDocument(MIPS_INDIVIDUAL, false, true);
		Node testParentNode = new Node();

		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertThat(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
			.isEqualTo(TEST_TIN);
		assertThat(testParentNode.getValue(NATIONAL_PROVIDER_IDENTIFIER))
			.isEqualTo(TEST_NPI);
		assertThat(testParentNode.getValue(MVP_ID))
			.isEqualTo(TEST_MVP_ID);
	}

	@Test
	void decodeMvpMipsGroupTest() {
		Element clinicalDocument = makeClinicalDocument(MIPS_GROUP, false, true);
		Node testParentNode = new Node();

		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertThat(testParentNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER))
			.isEqualTo(TEST_TIN);
		assertThat(testParentNode.getValue(MVP_ID))
			.isEqualTo(TEST_MVP_ID);
	}

	@Test
	void decodeMvpMipsSubgroupTest() {
		Element clinicalDocument = makeClinicalDocument(MIPS_SUBGROUP, true, true);
		Node testParentNode = new Node();

		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder(new Context());
		objectUnderTest.setNamespace(clinicalDocument.getNamespace());
		objectUnderTest.decode(clinicalDocument, testParentNode);

		assertThat(testParentNode.getValue(ENTITY_TYPE))
			.isEqualTo(ENTITY_SUBGROUP);
		assertThat(testParentNode.getValue(SUBGROUP_ID))
			.isEqualTo(TEST_SUBGROUP_ID);
		assertThat(testParentNode.getValue(MVP_ID))
			.isEqualTo(TEST_MVP_ID);
	}

	private Element makeClinicalDocument(String programName, boolean isSubgroup, boolean isMvp) {
		Namespace rootns = Namespace.getNamespace("urn:hl7-org:v3");
		Namespace ns = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element clinicalDocument = new Element("ClinicalDocument", rootns);
		clinicalDocument.addNamespaceDeclaration(ns);
		Element informationRecipient = prepareInfoRecipient(rootns, programName);
		if (isMvp) {
			clinicalDocument.addContent(prepareParticipant(rootns, "2.16.840.1.113883.3.249.5.6", TEST_MVP_ID));
		}
		if (isSubgroup) {
			clinicalDocument.addContent(prepareSubgroupDocumentationElement(rootns));
		} else {
			clinicalDocument.addContent(prepareDocumentationElement(rootns));
		}
		Element component = prepareComponentElement(rootns);

		clinicalDocument.addContent(informationRecipient);
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
	private Element prepareParticipant(Namespace rootns, String root, String id) {
		Element participant = new Element("participant", rootns);
		Element associatedEntity = new Element("associatedEntity", rootns);

		Element entityId = new Element("id", rootns)
			.setAttribute("root", root)
			.setAttribute("extension", id)
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
		Element performer2 = new Element("performer", rootns);
		Element assignedEntity = new Element("assignedEntity", rootns);
		Element assignedEntity2 = new Element("assignedEntity", rootns);
		Element nationalProviderIdentifier = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.4.6")
				.setAttribute("extension", "2567891421");
		Element nationalProviderIdentifier2 = new Element("id", rootns)
			.setAttribute("root", "2.16.840.1.113883.4.6")
			.setAttribute("extension", "0007891421");
		Element virtualGroup = new Element("id", rootns)
			.setAttribute("root", "2.16.840.1.113883.3.249.5.2")
			.setAttribute("extension", "x12345");
		Element virtualGroup2 = new Element("id", rootns)
			.setAttribute("root", "2.16.840.1.113883.3.249.5.2")
			.setAttribute("extension", "x12345");
		Element apmEntity = new Element("id", rootns)
			.setAttribute("root", "2.16.840.1.113883.3.249.5.4")
			.setAttribute("extension", "TEST_APM");

		Element representedOrganization = prepareRepOrgWithTaxPayerId(rootns, "123456789");
		representedOrganization.addContent(virtualGroup);
		representedOrganization.addContent(apmEntity);
		assignedEntity.addContent(representedOrganization);
		assignedEntity.addContent(nationalProviderIdentifier);
		performer.addContent(assignedEntity);

		Element representedOrganization2 = prepareRepOrgWithTaxPayerId(rootns, "222222222");
		representedOrganization2.addContent(virtualGroup2);
		assignedEntity2.addContent(representedOrganization2);
		assignedEntity2.addContent(nationalProviderIdentifier2);
		performer2.addContent(assignedEntity2);

		serviceEvent.addContent(performer);
		serviceEvent.addContent(performer2);
		documentationOf.addContent(serviceEvent);
		return documentationOf;
	}

	private Element prepareRepOrgWithTaxPayerId(Namespace rootns, String taxId) {
		Element representedOrganization = new Element("representedOrganization", rootns);
		Element taxpayerIdentificationNumber = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.4.2")
				.setAttribute("extension", taxId);

		representedOrganization.addContent(taxpayerIdentificationNumber);
		return representedOrganization;
	}

	private Element prepareSubgroupDocumentationElement(Namespace rootns) {
		Element documentationOf = new Element("documentationOf", rootns);
		Element serviceEvent = new Element("serviceEvent", rootns);
		Element performer = new Element("performer", rootns);
		Element assignedEntity = new Element("assignedEntity", rootns);

		Element representedOrganization = prepareRepOrgWithSubgroupId(rootns, TEST_SUBGROUP_ID);
		assignedEntity.addContent(representedOrganization);
		performer.addContent(assignedEntity);

		serviceEvent.addContent(performer);
		documentationOf.addContent(serviceEvent);
		return documentationOf;
	}

	private Element prepareRepOrgWithSubgroupId(Namespace rootns, String subgroupId) {
		Element representedOrganization = new Element("representedOrganization", rootns);
		Element taxpayerIdentificationNumber = new Element("id", rootns)
			.setAttribute("root", "2.16.840.1.113883.3.249.5.5")
			.setAttribute("extension", subgroupId);

		representedOrganization.addContent(taxpayerIdentificationNumber);
		return representedOrganization;
	}

	private Element prepareComponentElement(Namespace rootns) {
		Element component = new Element("component", rootns);
		Element structuredBody = new Element("structuredBody", rootns);
		Element componentTwo = new Element("component", rootns);
		Element aciSectionElement = new Element("templateId", rootns);
		aciSectionElement.setAttribute("root", TemplateId.PI_SECTION_V3.getRoot());
		aciSectionElement.setAttribute("extension", TemplateId.PI_SECTION_V3.getExtension());

		componentTwo.addContent(aciSectionElement);
		structuredBody.addContent(componentTwo);
		component.addContent(structuredBody);
		return component;
	}
}