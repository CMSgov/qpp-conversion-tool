package gov.cms.qpp.conversion.decode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

/**
 * Test class for the IaMeasureDecoder
 */
class IaMeasureDecoderTest {
	String xmlFragment;

	@BeforeEach
	void setUp() throws IOException {
		xmlFragment = TestHelper.getFixture("IaSection.xml");
	}

	@Test
	void internalDecode() throws Exception {
		IaMeasureDecoder decoder = new IaMeasureDecoder(new Context());
		QrdaDecoderEngine engine = new QrdaDecoderEngine(new Context());
		Node root = engine.decode(XmlUtils.stringToDom(xmlFragment));

		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);
		Node measurePerformed = root.findFirstNode(TemplateId.MEASURE_PERFORMED);
		String value = measurePerformed.getValue("measurePerformed");

		assertThat(iaMeasure.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
		assertThat(measurePerformed.getType())
				.isEqualTo(TemplateId.MEASURE_PERFORMED);
		assertThat(value)
				.isEqualTo("Y");
	}

	@Test
	void missingChildTest() throws Exception {
		xmlFragment = removeChildFragment(xmlFragment);
		IaMeasureDecoder decoder = new IaMeasureDecoder(new Context());
		QrdaDecoderEngine engine = new QrdaDecoderEngine(new Context());

		Node root = engine.decode(XmlUtils.stringToDom(xmlFragment));
		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);

		assertThat(iaMeasure.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.IA_MEASURE);
		assertThat(iaMeasure.getChildNodes())
				.hasSize(0);
	}

	@Test
	void internalDecodeWithExtraXmlPasses() throws Exception {
		IaMeasureDecoder decoder = new IaMeasureDecoder(new Context());
		QrdaDecoderEngine engine = new QrdaDecoderEngine(new Context());
		xmlFragment = addExtraXml(xmlFragment);

		Node root = engine.decode(XmlUtils.stringToDom(xmlFragment));

		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);
		Node measurePerformed = root.findFirstNode(TemplateId.MEASURE_PERFORMED);
		String value = measurePerformed.getValue("measurePerformed");

		assertThat(iaMeasure.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
		assertThat(measurePerformed.getType())
				.isEqualTo(TemplateId.MEASURE_PERFORMED);
		assertThat(value)
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