package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class AciNumeratorDenominatorValueRoundTripTest {

	// we currently have a root placeholder node, so the numerator/denominator
	// is indented an extra level

	@Test
	void decodeAggregateCountAsNode() throws Exception {
		String xmlFragment = XmlUtils.buildString("<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
				"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"    <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
				"    <statusCode code=\"completed\"/>", "    <value xsi:type=\"INT\" value=\"600\"/>",
				"    <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
				"  </observation>", "</root>");

		Context context = new Context();
		Node numDenomNode = new QppXmlDecoder(context).decode(XmlUtils.stringToDom(xmlFragment));

		String xPathExpected = "/*[local-name() = 'root' and namespace-uri() = 'urn:hl7-org:v3']/*[local-name() = 'observation'" +
		                       " and namespace-uri() = 'urn:hl7-org:v3']";
		assertWithMessage("The XPath of the aggregate count node is incorrect")
				.that(numDenomNode.getChildNodes().get(0).getPath())
				.isEqualTo(xPathExpected);

		QppOutputEncoder encoder = new QppOutputEncoder(context);
		List<Node> nodes = new ArrayList<>();
		nodes.add(numDenomNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		String expected = "{\n  \"value\" : 600\n}";
		assertThat(sw.toString()).isEqualTo(expected);
	}
}
