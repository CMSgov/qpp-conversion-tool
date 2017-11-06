package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AutoDetectQrdaTest {

	private static final String EXPECTED_ERROR_1 = "The file is not a QRDA-III XML document";
	private static final String EXPECTED_ERROR_2 = "The XML file is an unknown document";

	private PrintStream stderr;

	@BeforeEach
	void setup() throws Exception {
		stderr = System.err;
	}

	@AfterEach
	void teardown() throws Exception {
		System.setErr(stderr);
	}

	@Test
	void testNoTemplateId() throws IOException, XmlException {
		//set-up
		String xmlFragment = IOUtils.toString(getStream("bogus-QDRA-III"), Charset.defaultCharset());

		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos1));

		//execute
		XmlInputDecoder.decodeXml(new Context(), XmlUtils.stringToDom(xmlFragment));

		//assert
		assertThat(baos1.toString()).contains(EXPECTED_ERROR_1);
		assertThat(baos1.toString()).contains(EXPECTED_ERROR_2);
	}

	@Test
	void testNoClinicalDocumentElement() throws IOException, XmlException {

		//set-up
		String xmlFragment = IOUtils.toString(getStream("bogus-QDRA-III-root"), Charset.defaultCharset());

		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos2));

		//execute
		XmlInputDecoder.decodeXml(new Context(), XmlUtils.stringToDom(xmlFragment));

		//assert
		assertThat(baos2.toString()).contains(EXPECTED_ERROR_1);
		assertThat(baos2.toString()).contains(EXPECTED_ERROR_2);
	}

	private InputStream getStream(String path) {
		return ClasspathHelper.contextClassLoader().getResourceAsStream(path);
	}
}
