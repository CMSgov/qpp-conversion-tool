package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.ConverterTestHelper;
import gov.cms.qpp.conversion.Converter;
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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class DefaultEncoderTest {

	@Test
	public void encodeAllNodes() throws Exception {
		InputStream stream = XmlUtils.fileToStream(Paths.get("../qrda-files/valid-QRDA-III.xml"));
		String xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());

		Converter converter = ConverterTestHelper.newMockConverter();
		Node node = new QppXmlDecoder(converter).decode(XmlUtils.stringToDom(xmlFragment));

		Node placeHolder = new Node(TemplateId.DEFAULT, node);
		node.addChildNode(placeHolder);
		JsonWrapper wrapper = new JsonWrapper();
		new QppOutputEncoder(converter).encode(wrapper, node);

		Assert.assertTrue(wrapper.toString().length() > 10);
	}

	@Test
	public void encodeDefaultNode() throws EncodeException {
		Node root = new Node(TemplateId.DEFAULT);
		Node placeHolder = new Node(TemplateId.PLACEHOLDER, root);
		root.addChildNode(placeHolder);
		JsonWrapper wrapper = new JsonWrapper();
		new DefaultEncoder("Default Encode test").internalEncode(wrapper, root);
		Assert.assertTrue(wrapper.toString().length() == 3);
	}
}
