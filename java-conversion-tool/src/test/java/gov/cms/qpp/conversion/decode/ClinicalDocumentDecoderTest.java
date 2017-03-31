package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
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
		//set-up
		Namespace rootns = Namespace.getNamespace("urn:hl7-org:v3");
		Namespace ns = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element clinicalDocument = new Element("ClinicalDocument", rootns);
		clinicalDocument.addNamespaceDeclaration(ns);

		// Program name
		//"./ns:informationRecipient/ns:intendedRecipient/ns:id[@root='2.16.840.1.113883.3.249.7']/@extension"
		Element informationRecipient = new Element("informationRecipient", rootns);
		Element intendedRecipient = new Element("intendedRecipient", rootns);
		Element programName = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.3.249.7")
				.setAttribute("extension", "MIPS");
		intendedRecipient.addContent(programName);
		informationRecipient.addContent(intendedRecipient);
		clinicalDocument.addContent(informationRecipient);

//			Element taxProviderElement = new Element("performer", rootns);
//			//"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension"; //setTaxProviderTaxIdOnNode
//			Element processComponentElement = new Element("component", rootns);
//			//"./ns:component/ns:structuredBody/ns:component"; // processComponentElement

		//NationalProviderId
		//"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:id[@root='2.16.840.1.113883.4.6']/@extension"; //setNationalProviderIdOnNode
		Element documentationOf = new Element("documentationOf", rootns);
		Element serviceEvent = new Element("serviceEvent", rootns);
		Element performer = new Element("performer", rootns);
		Element assignedEntity = new Element("assignedEntity", rootns);
		Element nationalProviderIdentifier = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.4.6")
				.setAttribute("extension", "2567891421");
		assignedEntity.addContent(nationalProviderIdentifier);
		performer.addContent(assignedEntity);
		serviceEvent.addContent(performer);
		documentationOf.addContent(serviceEvent);
		clinicalDocument.addContent(documentationOf);

		//TaxProvider
		//"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension";

		Element representedOrganization = new Element("representedOrganization", rootns);
		Element taxpayerIdentificationNumber = new Element("id", rootns)
				.setAttribute("root", "2.16.840.1.113883.4.2")
				.setAttribute("extension", "123456789");

		representedOrganization.addContent(taxpayerIdentificationNumber);
		clinicalDocument.addContent(representedOrganization);

		// Test
		Node thisNode = new Node();
		ClinicalDocumentDecoder objectUnderTest = new ClinicalDocumentDecoder();
		objectUnderTest.setNamespace(clinicalDocument, objectUnderTest);
		objectUnderTest.internalDecode(clinicalDocument, thisNode);
		assertThat("Clinical Document doesn't contains program name", thisNode.getValue("programName"), is("mips"));

		assertThat("Clinical Document doesn't contains national provider", thisNode.getValue("nationalProviderIdentifier"), is("2567891421"));


		/**            Element templateIdElement = new Element("templateId", rootns)
		 .setAttribute("root", "2.16.840.1.113883.10.20.27.3.28");
		 Element referenceElement = new Element("reference", rootns);
		 Element externalDocumentElement = new Element("externalDocument", rootns);
		 Element idElement = new Element("id", rootns).setAttribute("extension", MEASURE_ID);

		 externalDocumentElement.addContent(idElement);
		 referenceElement.addContent(externalDocumentElement);
		 element.addContent(templateIdElement);
		 element.addContent(referenceElement);
		 element.addNamespaceDeclaration(ns);

		 Node thisNode = new Node();

		 AciProportionMeasureDecoder objectUnderTest = new AciProportionMeasureDecoder();
		 objectUnderTest.setNamespace(element, objectUnderTest);

		 //execute
		 objectUnderTest.internalDecode(element, thisNode);

		 //assert
		 assertThat("measureId should be " + MEASURE_ID, thisNode.getValue("measureId"), is(MEASURE_ID));
		 */


	}
}
