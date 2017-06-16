package gov.cms.qpp.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AciProportionDenominatorRoundTripTest extends ConversionTestSuite {

	@Test
	public void parseAciNumeratorDenominatorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "		<!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\" />\n"
				+ "		<code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\" />\n"
				+ "		<statusCode code=\"completed\" />\n"
				+ "		<value xsi:type=\"CD\" code=\"DENOM\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" />\n"
				+ "		<!-- Denominator Count -->\n"
				+ "		<entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.3\" />\n"
				+ "				<code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\" />\n"
				+ "				<statusCode code=\"completed\" />\n"
				+ "				<value xsi:type=\"INT\" value=\"600\" />\n"
				+ "				<methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\" />\n"
				+ "			</observation>" + "		</entryRelationship>\n" + "	</observation>\n" + "</component>";

		Node numDenomNode = new QppXmlDecoder().decode(XmlUtils.stringToDom(xmlFragment));

		QppOutputEncoder encoder = new QppOutputEncoder();
		List<Node> nodes = new ArrayList<>();
		nodes.add(numDenomNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		String EXPECTED = "{\n  \"denominator\" : 600\n}";
		assertThat("expected encoder to return a representation of a denominator with a value", sw.toString(),
				is(EXPECTED));
	}
}
