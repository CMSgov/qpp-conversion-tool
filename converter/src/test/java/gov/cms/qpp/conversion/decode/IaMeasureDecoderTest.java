package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * Test class for the IaMeasureDecoder
 */
public class IaMeasureDecoderTest {
	String xmlFragment;

	@Before
	public void setUp() throws IOException {
		xmlFragment = TestHelper.getFixture("IaSection.xml");
	}

	@Test
	public void internalDecode() throws Exception {
		IaMeasureDecoder decoder = new IaMeasureDecoder(new Context());
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));

		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);
		Node measurePerformed = root.findFirstNode(TemplateId.MEASURE_PERFORMED);
		String value = measurePerformed.getValue("measurePerformed");

		assertWithMessage("Should contain the correct value")
				.that(iaMeasure.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
		assertWithMessage("Should contain the correct template id")
				.that(measurePerformed.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.MEASURE_PERFORMED);
		assertWithMessage("The ACI_MEASURE_PERFORMED value should be \"Y\"")
				.that(value)
				.isEqualTo("Y");
	}

	@Test
	public void missingChildTest() throws Exception {
		xmlFragment = removeChildFragment(xmlFragment);
		IaMeasureDecoder decoder = new IaMeasureDecoder(new Context());

		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));
		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);

		assertWithMessage("IAMeasure node should be IA_MEASURE ")
				.that(iaMeasure.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.IA_MEASURE);
		assertWithMessage("There should not be any child node")
				.that(iaMeasure.getChildNodes())
				.hasSize(0);
	}

	@Test
	public void internalDecodeWithExtraXmlPasses() throws Exception {
		IaMeasureDecoder decoder = new IaMeasureDecoder(new Context());
		xmlFragment = addExtraXml(xmlFragment);
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));

		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);
		Node measurePerformed = root.findFirstNode(TemplateId.MEASURE_PERFORMED);
		String value = measurePerformed.getValue("measurePerformed");

		assertWithMessage("Should contain the correct value")
				.that(iaMeasure.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
		assertWithMessage("Should contain the correct template id")
				.that(measurePerformed.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.MEASURE_PERFORMED);
		assertWithMessage("The MEASURE_PERFORMED value should be \"Y\"")
				.that(value)
				.isEqualTo("Y");
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