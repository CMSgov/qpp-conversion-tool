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
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III-abridged.xml");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		assertThat("returned node should not be null", root	, is(not(nullValue())));
		
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
	

}
