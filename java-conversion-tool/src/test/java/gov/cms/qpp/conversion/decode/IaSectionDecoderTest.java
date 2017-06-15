package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class IaSectionDecoderTest extends ConversionTestSuite {
	private String xmlFragment;

	@Before
	public void setUp() throws IOException {
		xmlFragment = getFixture("IaSection.xml");
	}

	@Test
	public void decodeAciSectionAsNode() throws XmlException {
		Node root = executeDecoderWithoutDefaults();

		Node iaSectionNode = root.findFirstNode(TemplateId.IA_SECTION);

		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));
	}

	@Test
	public void testDecodeIaSectionIgnoresGarbage() throws XmlException {
		xmlFragment = xmlFragment.replaceAll("<statusCode ",
				"\n<Stuff arbitrary=\"123\"><newnode>Some extra stuff</newnode></Stuff>" +
						"Unexpected stuff appears here\n\n<statusCode ");

		Node root = executeDecoderWithoutDefaults();
		Node iaSectionNode = root.findFirstNode(TemplateId.IA_SECTION);

		assertThat("returned category", iaSectionNode.getValue("category"), is("ia"));
	}

	private Node executeDecoderWithoutDefaults() throws XmlException {
		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));
		DefaultDecoder.removeDefaultNode(root.getChildNodes());
		return root;
	}
}
