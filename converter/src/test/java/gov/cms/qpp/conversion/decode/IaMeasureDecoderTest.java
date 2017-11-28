package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

class IaMeasureDecoderTest {
	String xmlFragment;

	@BeforeEach
	void setUp() throws IOException {
		xmlFragment = TestHelper.getFixture("IaSection.xml");
	}

	@Test
	void testDecodeReturnsMeasureId() throws Exception {
		Node iaMeasure = internalDecodeIaMeasure();

		assertWithMessage("Should contain the correct value")
				.that(iaMeasure.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
	}

	@Test
	void testDecodeIaMeasureNodeContainsCorrectChild() throws Exception {
		Node iaMeasure = internalDecodeIaMeasure();
		Node measurePerformed = iaMeasure.findFirstNode(TemplateId.MEASURE_PERFORMED);

		assertWithMessage("Should contain the correct child node")
				.that(measurePerformed.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.MEASURE_PERFORMED);
	}

	@Test
	void testMissingMeasurePerformedFromIaMeasureSuccess() throws Exception {
		xmlFragment = removeChildFragment(xmlFragment);
		Node iaMeasure = internalDecodeIaMeasure();

		assertWithMessage("There should not be any child node")
				.that(iaMeasure.getChildNodes())
				.hasSize(0);
	}

	@Test
	void testDecodeWithExtraXmlReturnsCorrectMeasureId() throws Exception {
		xmlFragment = addExtraXml(xmlFragment);
		Node iaMeasure = internalDecodeIaMeasure();

		assertWithMessage("Should contain the correct value")
				.that(iaMeasure.getValue("measureId"))
				.isEqualTo("IA_EPA_1");
	}

	@Test
	void testDecodeWithExtraXmlReturnsCorrectChildNode() throws Exception {
		xmlFragment = addExtraXml(xmlFragment);
		Node iaMeasure = internalDecodeIaMeasure();
		Node measurePerformed = iaMeasure.findFirstNode(TemplateId.MEASURE_PERFORMED);

		assertWithMessage("Should contain the correct template id")
				.that(measurePerformed.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.MEASURE_PERFORMED);
	}

	private Node internalDecodeIaMeasure() throws Exception {
		IaMeasureDecoder decoder = new IaMeasureDecoder(new Context());
		Node root = decoder.decode(XmlUtils.stringToDom(xmlFragment));
		Node iaMeasure = root.findFirstNode(TemplateId.IA_MEASURE);

		return iaMeasure;
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