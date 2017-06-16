package gov.cms.qpp.conversion.decode.placeholder;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class DefaultDecoderTest {

	@Test
	public void parseAllNodes() throws Exception {

		InputStream stream = XmlUtils.fileToStream(Paths.get("../qrda-files/valid-QRDA-III.xml"));
		String xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());

		Node node = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));

		Assert.assertNotNull(node);
	}
}
