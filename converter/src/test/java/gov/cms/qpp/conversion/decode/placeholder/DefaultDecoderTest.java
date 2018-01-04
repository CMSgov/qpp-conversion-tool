package gov.cms.qpp.conversion.decode.placeholder;

import static com.google.common.truth.Truth.assertThat;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

class DefaultDecoderTest {

	@Test
	void parseAllNodes() throws Exception {
		InputStream stream = XmlUtils.fileToStream(Paths.get("../qrda-files/valid-QRDA-III.xml"));
		String xmlFragment = IOUtils.toString(stream, StandardCharsets.UTF_8);

		Node node = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		assertThat(node).isNotNull();
	}

	@Test
	void testInternalDecode() {
		DefaultDecoder decoder = new DefaultDecoder(new Context(), "mock");
		Node node = new Node();
		decoder.internalDecode(null, node);
		Truth.assertThat(node.getValue("DefaultDecoderFor")).isEqualTo("mock");
	}

	@Test
	void testRemoveDefaultNode() {
		List<Node> nodes = Stream.generate(Node::new).limit(5).collect(Collectors.toList());
		nodes.get(0).putValue("DefaultDecoderFor", "mock");
		DefaultDecoder.removeDefaultNode(nodes);
		Truth.assertThat(nodes).hasSize(4);
	}
}
