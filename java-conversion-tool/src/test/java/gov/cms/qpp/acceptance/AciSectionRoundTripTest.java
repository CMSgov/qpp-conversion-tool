package gov.cms.qpp.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AciSectionRoundTripTest {

	private static final String EXPECTED = "{\n  \"category\" : \"aci\",\n  \"measurements\" : [ "
			+ "{\n    \"measure\" : \"measure1\"\n  } ]\n}";

	@Test
	public void parseAciNumeratorDenominatorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<section>\n" + "		<!-- Measure Section -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.24.2.2\"/>\n"
				+ "		<!-- Advancing Care Information Section templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.2.5\" extension=\"2016-09-01\"/>\n"
				+ "		<code code=\"55186-1\" codeSystem=\"2.16.840.1.113883.6.1\" displayName=\"Measure Section\"/>\n"
				+ "		<title>Measure Section</title>\n" + "		<text>\n" + "		</text>\n" + "		<entry>\n"
				+ "			<qed resultName=\"measure\" resultValue=\"measure1\">\n"
				+ "				<templateId root=\"Q.E.D\"/>\n" + "			</qed>" + "		</entry>\n"
				+ "	</section>\n" + "</component>";

		Node measureNode = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		QppOutputEncoder encoder = new QppOutputEncoder();
		List<Node> nodes = new ArrayList<>();
		nodes.add(measureNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		assertThat("expected encoder to return a representation of a measure", sw.toString(), is(EXPECTED));

	}

}
