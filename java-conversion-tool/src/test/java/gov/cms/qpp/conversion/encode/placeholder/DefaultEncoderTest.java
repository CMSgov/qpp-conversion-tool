package gov.cms.qpp.conversion.encode.placeholder;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class DefaultEncoderTest {

	@Before
	public void setup() throws Exception {
		Validations.init();
	}
	
	@After
	public void teardown() throws Exception {
		Validations.clear();
	}
	
	@Test
	public void encodeAllNodes() throws Exception {
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO"); // DEBUG for tracking the decode
		
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III.xml");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		Node node = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		JsonWrapper wrapper = new JsonWrapper();
		new QppOutputEncoder().encode(wrapper, node);
		
		System.err.println(wrapper);
	}
}
