package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.io.IOException;

public class IaSectionDecoderTest extends BaseTest {
	private String xmlFragment;

	@Before
	public void setUp() throws IOException {
		xmlFragment = getFixture("IaSection.xml");
	}

	@Test
	public void decodeAciSectionAsNode() throws XmlException {
		Node root = executeDecoderWithoutDefaults();

		Node iaSectionNode = root.findFirstNode(TemplateId.IA_SECTION.getTemplateId());

		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));
	}

	@Test
	public void testDecodeIaSectionContainsIaMeasureSectionId() throws XmlException {
		Node root = executeDecoderWithoutDefaults();

		Node iaMeasureNode = root.findFirstNode(TemplateId.IA_MEASURE.getTemplateId());

		assertThat("returned should have measureId", iaMeasureNode.getValue("measureId"), is("IA_EPA_1"));
	}

	@Test
	public void testDecodeIaMeasureSectionContainsMeasurePerformed() throws XmlException {
		Node root = executeDecoderWithoutDefaults();

		Node iaMeasurePerformedNode = root.findFirstNode(TemplateId.MEASURE_PERFORMED.getTemplateId());
		assertThat("returned measurePerformed", iaMeasurePerformedNode.getValue("measurePerformed"), is("Y"));
	}

	@Test
	public void testDecodeIaSectionIgnoresGarbage() throws XmlException {
		xmlFragment = xmlFragment.replaceAll("<statusCode ",
				"\n<Stuff arbitrary=\"123\"><newnode>Some extra stuff</newnode></Stuff>Unexpected stuff appears here\n\n<statusCode ");

		Node root = executeDecoderWithoutDefaults();
		Node iaSectionNode = root.findFirstNode(TemplateId.IA_SECTION.getTemplateId());

		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));
	}

	private Node executeDecoderWithoutDefaults() throws XmlException {
		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));
		DefaultDecoder.removeDefaultNode(root.getChildNodes());
		return root;
	}
}
