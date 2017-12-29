package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class DefaultEncoderTest {

	@Test
	void encodeAllNodes() throws Exception {
		InputStream stream = XmlUtils.fileToStream(Paths.get("../qrda-files/valid-QRDA-III.xml"));
		String xmlFragment = IOUtils.toString(stream, StandardCharsets.UTF_8);

		Node node = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		Node placeHolder = new Node(TemplateId.DEFAULT, node);
		node.addChildNode(placeHolder);
		JsonWrapper wrapper = new JsonWrapper();
		new QppOutputEncoder(new Context()).encode(wrapper, node);

		assertThat(wrapper.toString().length() > 10).isTrue();
	}

	@Test
	void encodeDefaultNode() throws EncodeException {
		Node root = new Node(TemplateId.DEFAULT);
		Node placeHolder = new Node(TemplateId.PLACEHOLDER, root);
		root.addChildNode(placeHolder);
		JsonWrapper wrapper = new JsonWrapper();
		new DefaultEncoder("Default Encode test").internalEncode(wrapper, root);
		assertThat(wrapper.toString()).hasLength(3);
	}
}
