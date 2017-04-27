package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.Charset;

public class DefaultEncoderTest {
	
	@Test
	public void encodeAllNodes() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III.xml");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		Node node = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));

		JsonWrapper wrapper = new JsonWrapper();
		new QppOutputEncoder().encode(wrapper, node);
		
		Assert.assertTrue(wrapper.toString().length() > 10 );
	}
}
