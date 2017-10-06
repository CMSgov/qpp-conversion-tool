package gov.cms.qpp.conversion.decode.placeholder;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class DefaultDecoderTest {

	@Test
	public void parseAllNodes() throws Exception {
		InputStream stream = XmlUtils.fileToStream(Paths.get("../qrda-files/valid-QRDA-III.xml"));
		String xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());

		Node node = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		assertThat(node).isNotNull();
	}
}
