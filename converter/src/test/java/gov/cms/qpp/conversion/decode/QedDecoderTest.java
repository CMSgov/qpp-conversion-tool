package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class QedDecoderTest {

	@Test
	public void decodeQEDAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<qed resultName=\"result\" resultValue=\"mytestvalue\">\n"
				+ "		<templateId root=\"Q.E.D\"/>\n"
				+ "	</qed>"
				+ "</root>";
	
		// Get the root wrapper node
		Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		assertWithMessage("root node should have one child node")
				.that(root.getChildNodes())
				.hasSize(1);

		Node target = root.getChildNodes().get(0);

		assertWithMessage("test value should be mytestvalue")
				.that(target.getValue("result"))
				.isEqualTo("mytestvalue");
	}

}
