package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ClinicalDocumentDecoderTest {

	@Test
	public void decodeClinicalDocumentDecoderAsNode() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III-abridged.xml");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(root.getChildNodes());

		assertThat("returned node should not be null", root, is(not(nullValue())));

		assertThat("template ID is correct", root.getId(), is("2.16.840.1.113883.10.20.27.1.2"));
		assertThat("programName is correct", root.getValue("programName"), is("mips"));
		assertThat("nationalProviderIdentifier correct", root.getValue("nationalProviderIdentifier"), is("2567891421"));
		assertThat("taxpayerIdentificationNumber correct", root.getValue("taxpayerIdentificationNumber"), is("123456789"));

		assertThat("returned node should not be null", root, is(not(nullValue())));

		// System.out.println(root.toString());
		assertThat("returned node should child decoder nodes", root.getChildNodes().size(), is(3));

		Node reportParameterSectionNode = root.getChildNodes().get(0);

		assertThat("returned category", reportParameterSectionNode.getValue("source"), is("provider"));

		// Should have a Measure node 
		assertThat("returned node should not be null", reportParameterSectionNode.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", reportParameterSectionNode.getChildNodes().size(), is(1));
		Node reportingActSectionNodeMeasureNode = reportParameterSectionNode.getChildNodes().get(0);
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceStart"), is("20170101"));
		assertThat("returned should value", reportingActSectionNodeMeasureNode.getValue("performanceEnd"), is("20171231"));


		Node aciSectionNode = root.getChildNodes().get(1);

		// Should have an ACI section node 
		assertThat("returned category should be aci", aciSectionNode.getValue("category"), is("aci"));
		assertThat("returned node should have child decoder nodes", aciSectionNode.getChildNodes().size(), is(3));
		assertThat("returned measureId ACI-PEA-1", aciSectionNode.getChildNodes().get(0).getValue("measureId"), is("ACI-PEA-1"));
		assertThat("returned measureId ACI_EP_1", aciSectionNode.getChildNodes().get(1).getValue("measureId"), is("ACI_EP_1"));
		assertThat("returned measureId ACI_CCTPE_3", aciSectionNode.getChildNodes().get(2).getValue("measureId"), is("ACI_CCTPE_3"));


		// Should have an IA section node 
		Node iaSectionNode = root.getChildNodes().get(2);
		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));

		// Should have a Measure node 
		assertThat("returned node should not be null", iaSectionNode.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", iaSectionNode.getChildNodes().size(), is(1));
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertThat("returned should have measureId", iaMeasureNode.getValue("measureId"), is("IA_EPA_1"));

		// Should have a measure performed node 
		assertThat("returned node should not be null", iaMeasureNode.getChildNodes(), is(not(nullValue())));
		assertThat("returned node should have one child decoder node", iaMeasureNode.getChildNodes().size(), is(1));
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
		aciSectionElement.setAttribute("root", "2.16.840.1.113883.10.20.27.2.5");

		componentTwo.addContent(aciSectionElement);
		structuredBody.addContent(componentTwo);
		component.addContent(structuredBody);
		return component;
	}
}
