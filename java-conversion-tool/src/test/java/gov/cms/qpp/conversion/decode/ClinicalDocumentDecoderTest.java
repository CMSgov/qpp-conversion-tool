package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

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
		assertThat("template ID is correct", root.getId(), is("2.16.840.1.113883.10.20.27.1.2"));
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

}
