package gov.cms.qpp.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ClinicalDocumentRoundTripTest {

	private static final String EXPECTED = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\"," + "\n  \"performanceYear\" : 2017,"
			+ "\n  \"measurementSets\" : [ " + "{\n    \"category\" : \"aci\",\n    \"measurements\" : [ "
			+ "{\n      \"measureId\" : \"ACI-PEA-1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 600,\n        \"denominator\" : 800\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_EP_1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 500,\n        \"denominator\" : 700\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_CCTPE_3\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    } ],"
			+ "\n    \"source\" : \"provider\"," + "\n    \"performanceStart\" : \"20170101\","
			+ "\n    \"performanceEnd\" : \"20171231\"" + "\n  } ]\n}";

	@Test
	public void parseAciNumeratorDenominatorAsNode() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III.xml");
		String xmlString = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());

		Element dom = XmlUtils.stringToDOM(xmlString);

		QppXmlDecoder parser = new QppXmlDecoder();
		parser.setDom(dom);

		Node clinicalDocumentNode = parser.decode();

		QppOutputEncoder encoder = new QppOutputEncoder();
		List<Node> nodes = new ArrayList<>();
		nodes.add(clinicalDocumentNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		assertThat("expected encoder to return a representation of a clinical document", sw.toString(), is(EXPECTED));

	}

}
