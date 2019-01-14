package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.decode.XmlDecoderEngine;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

class ClinicalDocumentRoundTripTest {

	@Test
	void parseClinicalDocument() throws Exception {
		String expectedRaw = TestHelper.getFixture("clinicalDocument.json");
		ObjectReader reader = new ObjectMapper().reader();
		JsonNode expected = reader.readTree(expectedRaw);

		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III-abridged.xml");
		String xmlFragment = IOUtils.toString(stream, StandardCharsets.UTF_8);

		Context context = new Context();
		Node clinicalDocumentNode = XmlDecoderEngine.decodeXml(context, XmlUtils.stringToDom(xmlFragment));

		// remove default nodes (will fail if defaults change)

		QppOutputEncoder encoder = new QppOutputEncoder(context);
		encoder.setNodes(Collections.singletonList(clinicalDocumentNode));

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw), true);
		JsonNode actual = reader.readTree(sw.toString());

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void checkCorrectClinicalDocumentTemplateIdWins() throws XmlException {
		String similarClinicalDocumentBlob = "<ClinicalDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "\t\t\t\t  xsi:schemaLocation=\"urn:hl7-org:v3 ../CDA_Schema_Files/infrastructure/cda/CDA_SDTC.xsd\"\n"
			+ "\t\t\t\t  xmlns=\"urn:hl7-org:v3\" xmlns:voc=\"urn:hl7-org:v3/voc\">\n"
			+ "\t<realmCode code=\"US\"/>\n"
			+ "\t<typeId root=\"2.16.840.1.113883.1.3\" extension=\"POCD_HD000040\"/>\n"
			+ "\t<templateId root=\"2.16.840.1.113883.10.20.27.1.2\"/>\n"
			+ "\t<templateId root=\"2.16.840.1.113883.10.20.27.1.2\" extension=\"2017-07-01\"/>\n"
			+ "</ClinicalDocument>";

		Node root = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(similarClinicalDocumentBlob));

		assertThat(root.getType()).isEqualTo(TemplateId.CLINICAL_DOCUMENT);
	}
}
