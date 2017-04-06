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

	private PrintStream err;

	private static final String EXPECTED_ERROR = "[main] ERROR gov.cms.qpp.conversion.decode.QppXmlDecoder - The " +
	                                             "file is not a QRDA-III XML document\n[main] " +
	                                             "ERROR gov.cms.qpp.conversion.decode.XmlInputDecoder - The XML file " +
	                                             "is an unknown document\n";
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() throws Exception {
		Validations.init();
		System.setProperty("line.separator", "\n");
		err = System.err;
	}

	@After
	public void teardown() throws Exception {
		System.setErr(err);
		System.setProperty("line.separator", System.lineSeparator());
		Validations.clear();
	}

	@Test
	public void testNoTemplateId() throws IOException, XmlException {

		//set-up
		ClassPathResource xmlResource = new ClassPathResource("bogus-QDRA-III");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos1));

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
		System.setErr(new PrintStream(baos2));

		//execute
		XmlInputDecoder.decodeXml(XmlUtils.stringToDOM(xmlFragment));

		//assert
		assertThat("Incorrect error message", baos2.toString(), is(EXPECTED_ERROR));
	}
}
