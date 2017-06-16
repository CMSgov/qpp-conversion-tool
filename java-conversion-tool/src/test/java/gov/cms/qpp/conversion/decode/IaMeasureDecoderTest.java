package gov.cms.qpp.conversion.decode;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Test class for the IaMeasureDecoder
 */
public class IaMeasureDecoderTest extends ConversionTestSuite {
	String xmlFragment;

	@Before
	public void setUp() throws IOException {
		xmlFragment = getFixture("IaSection.xml");
	}

	@Test
	public void internalDecode() throws Exception {
		IaMeasureDecoder decoder = new IaMeasureDecoder();
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));

		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);
		Node measurePerformed = root.findFirstNode(TemplateId.MEASURE_PERFORMED);
		String value = measurePerformed.getValue("measurePerformed");

		assertThat("Should contain the correct value", iaMeasure.getValue("measureId"),
				is("IA_EPA_1"));
		assertThat("Should contain the correct template id", measurePerformed.getType(),
				is(TemplateId.MEASURE_PERFORMED));
		assertThat("The ACI_MEASURE_PERFORMED value should be \"Y\"" , value, is("Y"));
	}

	@Test
	public void missingChildTest() throws Exception {
		xmlFragment = removeChildFragment(xmlFragment);
		IaMeasureDecoder decoder = new IaMeasureDecoder();

		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));
		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);

		assertThat("IAMeasure node should be IA_MEASURE ", iaMeasure.getType(),
				is(TemplateId.IA_MEASURE));
		assertThat("There should not be any child node", iaMeasure.getChildNodes().size(), is(0));
	}

	@Test
	public void internalDecodeWithExtraXmlPasses() throws Exception {
		IaMeasureDecoder decoder = new IaMeasureDecoder();
		xmlFragment = addExtraXml(xmlFragment);
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));

		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);
		Node measurePerformed = root.findFirstNode(TemplateId.MEASURE_PERFORMED);
		String value = measurePerformed.getValue("measurePerformed");

		assertThat("Should contain the correct value", iaMeasure.getValue("measureId"),
				is("IA_EPA_1"));
		assertThat("Should contain the correct template id", measurePerformed.getType(),
				is(TemplateId.MEASURE_PERFORMED));
		assertThat("The MEASURE_PERFORMED value should be \"Y\"" , value, is("Y"));
	}

	private String addExtraXml(String source) {
		int start = source.indexOf("<observation");
		int end = source.indexOf(">", start) + 1;
		StringBuilder extraXml = new StringBuilder();
		extraXml.append(source.substring(0, end));
		extraXml.append(" SOME EXTRA STUFF GOES HERE <An unexpected=\"tag\"/> and yet more stuff\n");
		extraXml.append(source.substring(end));
		return extraXml.toString();
	}

	private String removeChildFragment(String source) {
		int start = source.indexOf("<component>");
		int end = source.indexOf("</component>") + 12;
		StringBuilder component = new StringBuilder();
		component.append(source.substring(0, start));
		component.append(source.substring(end));
		return component.toString();
	}
}