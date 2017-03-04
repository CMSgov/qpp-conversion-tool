package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ClinicalDocumentDecoderTest {

	@Test
	public void decodeClinicalDocumentDecoderAsNode() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III.xml");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		assertThat("returned node should not be null", root	, is(not(nullValue())));
		
		assertThat("template ID is correct", root.getId(), is("2.16.840.1.113883.10.20.27.1.2"));
		assertThat("programName is correct", root.getValue("programName"), is("mips"));
		assertThat("nationalProviderIdentifier correct", root.getValue("nationalProviderIdentifier"), is("2567891421"));
		assertThat("taxpayerIdentificationNumber correct", root.getValue("taxpayerIdentificationNumber"), is("123456789"));
		assertThat("performanceStart  correct", root.getValue("performanceStart"), is("20170101"));
		assertThat("performanceEnd correct", root.getValue("performanceEnd"), is("20171231"));

		assertThat("returned node should not be null", root, is(not(nullValue())));
		
		System.out.println(root.toString());
		assertThat("returned node should have one child decoder node", root.getChildNodes().size(), is(2));
		Node aciSectionNode = root.getChildNodes().get(0);

		// Should have a section node 
		assertThat("returned category should be aci", aciSectionNode.getValue("category"), is("aci"));
		// Should have a measurement nodes
		assertThat("returned node should have one child decoder node", aciSectionNode.getChildNodes().size(), is(3));
		// Should have a section node 
		assertThat("returned measureId ACI-PEA-1", aciSectionNode.getChildNodes().get(0).getValue("measureId"), is("ACI-PEA-1"));
		// Should have a section node 
		assertThat("returned measureId ACI_EP_1", aciSectionNode.getChildNodes().get(1).getValue("measureId"), is("ACI_EP_1"));
		// Should have a section node 
		assertThat("returned measureId ACI_CCTPE_3", aciSectionNode.getChildNodes().get(2).getValue("measureId"), is("ACI_CCTPE_3"));


		Node iaSectionNode = root.getChildNodes().get(1);
		// Should have a section node 
		assertThat("returned category should be ia", iaSectionNode.getValue("category"), is("ia"));
		// Should have a measure node
		assertThat("returned node should have one child decoder node", iaSectionNode.getChildNodes().size(), is(1));
		Node iaMeasureNode = iaSectionNode.getChildNodes().get(0);
		assertThat("returned measureId IA_EPA_1", iaMeasureNode.getValue("measureId"), is("IA_EPA_1"));
		assertThat("returned iaMeasured Y", iaMeasureNode.getValue("iaMeasured"), is("Y"));

	}
	

}
