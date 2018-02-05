package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.XmlDecoderEngine;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import gov.cms.qpp.test.logging.LoggerContract;

class AutoDetectQrdaTest implements LoggerContract {

	private static final String EXPECTED_ERROR_1 = "The file is not a QRDA-III XML document";

	@Test
	void testNoTemplateId() throws IOException, XmlException {
		//set-up
		String xmlFragment = IOUtils.toString(getStream("bogus-QDRA-III"), StandardCharsets.UTF_8);

		//execute
		clearLogs();
		assertThat(getLogs()).hasSize(0);
		XmlDecoderEngine.decodeXml(new Context(), XmlUtils.stringToDom(xmlFragment));

		//assert
		assertThat(getLogs()).containsExactly("The XML file is an unknown document");
	}

	@Test
	void testNoClinicalDocumentElement() throws IOException, XmlException {
		//set-up
		String xmlFragment = IOUtils.toString(getStream("bogus-QDRA-III-root"), StandardCharsets.UTF_8);

		//execute
		clearLogs();
		XmlDecoderEngine.decodeXml(new Context(), XmlUtils.stringToDom(xmlFragment));

		//assert
		assertThat(getLogs()).contains("The XML file is an unknown document");
	}

	private InputStream getStream(String path) {
		return ClasspathHelper.contextClassLoader().getResourceAsStream(path);
	}

	@Override
	public Class<?> getLoggerType() {
		return XmlDecoderEngine.class;
	}
}
