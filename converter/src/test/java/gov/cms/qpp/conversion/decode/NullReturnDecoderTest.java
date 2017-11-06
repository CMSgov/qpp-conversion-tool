package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class NullReturnDecoderTest {

	@Test
	void decodeReturnNullNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<null resultName=\"result\" resultValue=\"mytestvalue\">\n"
				+ "		<templateId root=\"null.return\"/>\n"
				+ "	</null>"
				+ "</root>";

		// Get the root wrapper node
		Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		// We get a placeholder when the decoder returns null Node
		assertWithMessage("root node should have one child node")
				.that(root.getType())
				.isEquivalentAccordingToCompareTo(TemplateId.PLACEHOLDER);
	}

}
