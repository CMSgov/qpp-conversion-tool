package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import java.io.InputStream;
import java.nio.charset.Charset;

public class DefaultEncoderTest {

	@Test
	public void encodeAllNodes() throws Exception {
		InputStream stream = ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III.xml");
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
