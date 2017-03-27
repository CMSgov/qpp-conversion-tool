package gov.cms.qpp.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AutoDetectQrdaTest {
	
	@Before
	public void setup() throws Exception {
		Validations.init();
		System.setProperty("line.separator", "\n");
	}
	
	@After
	public void teardown() throws Exception {
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		System.setProperty("line.separator", System.lineSeparator());
		Validations.clear();
	}
	
	
	@Test
	public void validationFileTest() throws Exception {
		String error1 = "[main] ERROR gov.cms.qpp.conversion.decode.QppXmlDecoder - The file is not a QDRA-III xml document\n";
		String error2 =	"[main] ERROR gov.cms.qpp.conversion.decode.XmlInputDecoder - The file is an unknown XML document\n";
		
		ClassPathResource xmlResource = new ClassPathResource("bogus-QDRA-III");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
		
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos1));
		
		XmlInputDecoder.decodeXml(XmlUtils.stringToDOM(xmlFragment));
		
		assertThat("Expected err mesage", baos1.toString(), is(error1));
		
 
		xmlResource = new ClassPathResource("bogus-QDRA-III-root");
		xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
		
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos2));
		
		XmlInputDecoder.decodeXml(XmlUtils.stringToDOM(xmlFragment));
		
		assertThat("Expected err mesage", baos2.toString(), is(error2));
		
		xmlResource = new ClassPathResource("non-xml-file.xml");
		xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
		
		try {
			XmlInputDecoder.decodeXml(XmlUtils.stringToDOM(xmlFragment));
		} catch (Exception e) {
			assertThat("Expected XmlException", e instanceof XmlException, is(true));
		}
		
	}
	
}
