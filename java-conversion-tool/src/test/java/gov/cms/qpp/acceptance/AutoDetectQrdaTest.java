package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AutoDetectQrdaTest {

	private PrintStream stdout;

	private static final String EXPECTED_ERROR =
		"ERROR - The file is not a QRDA-III XML document" + System.lineSeparator()+
		 "ERROR - The XML file is an unknown document" +System.lineSeparator();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() throws Exception {
		Validations.init();
		stdout = System.out;
	}

	@After
	public void teardown() throws Exception {
		System.setOut(stdout);
		Validations.clear();
	}

	@Test
	public void testNoTemplateId() throws IOException, XmlException {

		//set-up
		ClassPathResource xmlResource = new ClassPathResource("bogus-QDRA-III");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos1));

		//execute
		XmlInputDecoder.decodeXml(XmlUtils.stringToDOM(xmlFragment));

		//assert
		assertThat("Incorrect error message", baos1.toString(), is(EXPECTED_ERROR));
	}

	@Test
	public void testNoClinicalDocumentElement() throws IOException, XmlException {

		//set-up
		ClassPathResource xmlResource = new ClassPathResource("bogus-QDRA-III-root");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos2));

		//execute
		XmlInputDecoder.decodeXml(XmlUtils.stringToDOM(xmlFragment));

		//assert
		assertThat("Incorrect error message", baos2.toString(), is(EXPECTED_ERROR));
	}
}
