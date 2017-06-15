package gov.cms.qpp.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ClinicalDocumentRoundTripTest extends ConversionTestSuite {

	private static final String EXPECTED = "{\n  \"programName\" : \"mips\",\n  \"entityType\" : \"individual\",\n  "
		+ "\"taxpayerIdentificationNumber\" : \"123456789\",\n  \"nationalProviderIdentifier\" : \"2567891421\",\n  "
		+ "\"performanceYear\" : 2017,\n  \"measurementSets\" : [ {\n    \"category\" : \"aci\",\n    "
		+ "\"submissionMethod\" : \"electronicHealthRecord\",\n    \"measurements\" : [ {\n      "
		+ "\"measureId\" : \"ACI-PEA-1\",\n      \"value\" : {\n        \"numerator\" : 600,\n        "
		+ "\"denominator\" : 800\n      }\n    }, {\n      \"measureId\" : \"ACI_EP_1\",\n      \"value\" : {\n        "
		+ "\"numerator\" : 500,\n        \"denominator\" : 700\n      }\n    }, {\n      \"measureId\" : \"ACI_CCTPE_3\",\n      "
		+ "\"value\" : {\n        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    } ],\n    "
		+ "\"performanceStart\" : \"2017-01-01\",\n    \"performanceEnd\" : \"2017-12-31\"\n  }, {\n    "
		+ "\"category\" : \"ia\",\n    \"submissionMethod\" : \"electronicHealthRecord\",\n    \"measurements\" : [ {\n      "
		+ "\"measureId\" : \"IA_EPA_1\",\n      \"value\" : true\n    } ],\n    \"performanceStart\" : \"2017-01-01\",\n    "
		+ "\"performanceEnd\" : \"2017-12-31\"\n  } ]\n}";

	@Test
	public void parseClinicalDocument() throws Exception {
		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("valid-QRDA-III-abridged.xml");
		String xmlFragment = IOUtils.toString(stream, Charset.defaultCharset());

		Node clinicalDocumentNode = XmlInputDecoder.decodeXml(XmlUtils.stringToDom(xmlFragment));

		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(clinicalDocumentNode.getChildNodes());

		QppOutputEncoder encoder = new QppOutputEncoder();
		encoder.setNodes(Collections.singletonList(clinicalDocumentNode));

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		assertThat("expected encoder to return a representation of a clinical document", sw.toString(), is(EXPECTED));
	}

}
