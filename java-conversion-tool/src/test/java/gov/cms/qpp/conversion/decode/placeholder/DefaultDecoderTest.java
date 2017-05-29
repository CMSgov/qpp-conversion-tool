package gov.cms.qpp.conversion.decode.placeholder;

import gov.cms.qpp.conversion.decode.*;
import gov.cms.qpp.conversion.model.*;
import gov.cms.qpp.conversion.xml.*;
import org.apache.commons.io.*;
import org.junit.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

public class DefaultDecoderTest {

	@Test
	public void parseAllNodes() throws Exception {

		InputStream stream = XmlUtils.fileToStream(Paths.get("../qrda-files/valid-QRDA-III.xml"));
		String xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());

		Node node = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));

		Assert.assertNotNull(node);
	}
}
