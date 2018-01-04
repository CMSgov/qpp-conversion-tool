package gov.cms.qpp.acceptance;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

class ClinicalDocumentRoundTripTest {

	private static String expected;

	@BeforeAll
	static void setup() throws IOException {
		expected = TestHelper.getFixture("clinicalDocument.json");
	}

	@Test
	void parseClinicalDocument() throws Exception {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III-abridged.xml");
		String xmlFragment = IOUtils.toString(stream, StandardCharsets.UTF_8);

		Context context = new Context();
		Node clinicalDocumentNode = XmlInputDecoder.decodeXml(context, XmlUtils.stringToDom(xmlFragment));

		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(clinicalDocumentNode.getChildNodes());

		QppOutputEncoder encoder = new QppOutputEncoder(context);
		encoder.setNodes(Collections.singletonList(clinicalDocumentNode));

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		assertThat(sw.toString()).isEqualTo(expected);
	}

}
