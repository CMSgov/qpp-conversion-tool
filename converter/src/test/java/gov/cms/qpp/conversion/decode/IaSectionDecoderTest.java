package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class IaSectionDecoderTest {
	private String xmlFragment;

	@Before
	public void setUp() throws IOException {
		xmlFragment = TestHelper.getFixture("IaSection.xml");
	}

	@Test
	public void decodeAciSectionAsNode() throws XmlException {
		Node root = executeDecoderWithoutDefaults();

		Node iaSectionNode = root.findFirstNode(TemplateId.IA_SECTION);

		assertWithMessage("returned category")
				.that(iaSectionNode.getValue("category"))
				.isEqualTo("ia");
	}

	@Test
	public void testDecodeIaSectionIgnoresGarbage() throws XmlException {
		xmlFragment = xmlFragment.replaceAll("<statusCode ",
				"\n<Stuff arbitrary=\"123\"><newnode>Some extra stuff</newnode></Stuff>" +
						"Unexpected stuff appears here\n\n<statusCode ");

		Node root = executeDecoderWithoutDefaults();
		Node iaSectionNode = root.findFirstNode(TemplateId.IA_SECTION);

		assertWithMessage("returned category")
				.that(iaSectionNode.getValue("category"))
				.isEqualTo("ia");
	}

	private Node executeDecoderWithoutDefaults() throws XmlException {
		Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		DefaultDecoder.removeDefaultNode(root.getChildNodes());
		return root;
	}
}
