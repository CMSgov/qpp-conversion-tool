package gov.cms.qpp.conversion.decode.placeholder;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import java.io.InputStream;
import java.nio.charset.Charset;

public class DefaultDecoderTest {

	@Test
	public void parseAllNodes() throws Exception {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III.xml");
		String xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());

		Node node = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));

		Assert.assertNotNull(node);
	}
}
