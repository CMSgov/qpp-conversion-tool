package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reflections.util.ClasspathHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class AutoDetectQrdaTest {

	private static final String EXPECTED_ERROR_1 = "The file is not a QRDA-III XML document";
	private static final String EXPECTED_ERROR_2 = "The XML file is an unknown document";

	private PrintStream stderr;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() throws Exception {
		stderr = System.err;
	}

	@After
	public void teardown() throws Exception {
		System.setErr(stderr);
	}

	@Test
	public void testNoTemplateId() throws IOException, XmlException {

		//set-up
		String xmlFragment = IOUtils.toString(getStream("bogus-QDRA-III"), Charset.defaultCharset());

		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos1));

		//execute
		XmlInputDecoder.decodeXml(new Context(), XmlUtils.stringToDom(xmlFragment));

		//assert
		assertThat("Incorrect error message", baos1.toString(), allOf(containsString(EXPECTED_ERROR_1), containsString(EXPECTED_ERROR_2)));
	}

	@Test
	public void testNoClinicalDocumentElement() throws IOException, XmlException {

		//set-up
		String xmlFragment = IOUtils.toString(getStream("bogus-QDRA-III-root"), Charset.defaultCharset());

		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos2));

		//execute
		XmlInputDecoder.decodeXml(new Context(), XmlUtils.stringToDom(xmlFragment));

		//assert
		assertThat("Incorrect error message", baos2.toString(), allOf(containsString(EXPECTED_ERROR_1), containsString(EXPECTED_ERROR_2)));
	}

	private InputStream getStream(String path) {
		return ClasspathHelper.contextClassLoader().getResourceAsStream(path);
	}
}
