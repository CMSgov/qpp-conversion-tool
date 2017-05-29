package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.conversion.decode.*;
import gov.cms.qpp.conversion.encode.*;
import gov.cms.qpp.conversion.model.*;
import gov.cms.qpp.conversion.xml.*;
import org.apache.commons.io.*;
import org.junit.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

public class DefaultEncoderTest {

	@Test
	public void encodeAllNodes() throws Exception {
		InputStream stream = XmlUtils.fileToStream(Paths.get("../qrda-files/valid-QRDA-III.xml"));
		String xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());

		Node node = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));

		Node placeHolder = new Node(node, TemplateId.DEFAULT.getTemplateId());
		node.addChildNode(placeHolder);
		JsonWrapper wrapper = new JsonWrapper();
		new QppOutputEncoder().encode(wrapper, node);

		Assert.assertTrue(wrapper.toString().length() > 10);
	}

	@Test
	public void encodeDefaultNode() throws EncodeException {
		Node root = new Node(TemplateId.DEFAULT.getTemplateId());
		Node placeHolder = new Node(root, TemplateId.PLACEHOLDER.getTemplateId());
		root.addChildNode(placeHolder);
		JsonWrapper wrapper = new JsonWrapper();
		new DefaultEncoder("Default Encode test").internalEncode(wrapper, root);
		Assert.assertTrue(wrapper.toString().length() == 3);
	}
}
