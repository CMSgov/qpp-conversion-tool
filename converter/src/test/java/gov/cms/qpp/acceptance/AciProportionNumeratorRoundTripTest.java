package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AciProportionNumeratorRoundTripTest {

	@Test
	public void parseAciNumeratorDenominatorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.31\" extension=\"2016-09-01\" />\n"
				+ "		<code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\" />\n"
				+ "		<statusCode code=\"completed\" />\n"
				+ "		<value xsi:type=\"CD\" code=\"NUMER\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" />\n"
				+ "		<!-- Numerator Count -->\n"
				+ "		<entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.3\" />\n"
				+ "				<code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\" />\n"
				+ "				<statusCode code=\"completed\" />\n"
				+ "				<value xsi:type=\"INT\" value=\"600\" />\n"
				+ "				<methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\" />\n"
				+ "			</observation>\n" + "		</entryRelationship>\n" + "	</observation>\n" + "</component>";

		Context context = new Context();
		Node numDenomNode = new QppXmlDecoder(context).decode(XmlUtils.stringToDom(xmlFragment));

		QppOutputEncoder encoder = new QppOutputEncoder(context);
		List<Node> nodes = new ArrayList<>();
		nodes.add(numDenomNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		String EXPECTED = "{\n  \"numerator\" : 600\n}";
		assertThat("expected encoder to return a representation of a performanceMet with a value", sw.toString(),
				is(EXPECTED));
	}
}
