package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class AciNumeratorDenominatorRoundTripTest {

	@Test
	public void parseAciNumeratorDenominatorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<entry xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">\n"
				+ "		<!-- Implied template Measure Reference templateId -->\n"
				+ "		<!-- ACI Numerator Denominator Type Measure Reference and Results templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.28\" extension=\"2017-06-01\"/>\n"
				+ "		<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>\n"
				+ "		<statusCode code=\"completed\"/>\n" + "		<reference typeCode=\"REFR\">\n"
				+ "			<!-- Reference to a particular ACI measure's unique identifier. -->\n"
				+ "			<externalDocument classCode=\"DOC\" moodCode=\"EVN\">\n"
				+ "				<!-- This is a temporary root OID that indicates this is an ACI measure identifier -->\n"
				+ "				<!-- extension is the unique identifier for an ACI measure. \"ACI-PEA-1\" is for illustration only. -->\n"
				+ "				<id root=\"2.16.840.1.113883.3.7031\" extension=\"ACI-PEA-1\"/>\n"
				+ "				<!-- ACI measure title -->\n" + "				<text>Patient Access</text>\n"
				+ "			</externalDocument>\n" + "		</reference>\n" + "		<component>\n"
				+ "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<!-- Performance Rate templateId -->\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.30\"\n"
				+ "					extension=\"2016-09-01\"/>\n"
				+ "				<code code=\"72510-1\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Performance Rate\"/>\n"
				+ "				<statusCode code=\"completed\"/>\n"
				+ "				<value xsi:type=\"REAL\" value=\"0.750000\"/>\n" + "			</observation>\n"
				+ "		</component>\n" + "		<component>\n"
				+ "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<!-- ACI Numerator Denominator Type Measure Numerator Data templateId -->\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.31\"\n"
				+ "					extension=\"2016-09-01\"/>\n"
				+ "				<code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\"\n"
				+ "					codeSystemName=\"ActCode\" displayName=\"Assertion\"/>\n"
				+ "				<statusCode code=\"completed\"/>\n"
				+ "				<value xsi:type=\"CD\" code=\"NUMER\"\n"
				+ "					codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\"/>\n"
				+ "				<!-- Numerator Count-->\n"
				+ "				<entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "					<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "						<templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n"
				+ "						<code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\"\n"
				+ "							codeSystemName=\"ActCode\"\n"
				+ "							displayName=\"rate aggregation\"/>\n"
				+ "						<statusCode code=\"completed\"/>\n"
				+ "						<value xsi:type=\"INT\" value=\"600\"/>\n"
				+ "						<methodCode code=\"COUNT\"\n"
				+ "							codeSystem=\"2.16.840.1.113883.5.84\"\n"
				+ "							codeSystemName=\"ObservationMethod\"\n"
				+ "							displayName=\"Count\"/>\n" + "					</observation>\n"
				+ "				</entryRelationship>\n" + "			</observation>\n" + "		</component>\n"
				+ "		<component>\n" + "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.32\"\n"
				+ "					extension=\"2016-09-01\"/>\n"
				+ "				<code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\"\n"
				+ "					codeSystemName=\"ActCode\" displayName=\"Assertion\"/>\n"
				+ "				<statusCode code=\"completed\"/>\n"
				+ "				<value xsi:type=\"CD\" code=\"DENOM\"\n"
				+ "					codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\"/>\n"
				+ "				<!-- Denominator Count-->\n"
				+ "				<entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "					<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "						<templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n"
				+ "						<code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\"\n"
				+ "							codeSystemName=\"ActCode\"\n"
				+ "							displayName=\"rate aggregation\"/>\n"
				+ "						<statusCode code=\"completed\"/>\n"
				+ "						<value xsi:type=\"INT\" value=\"800\"/>\n"
				+ "						<methodCode code=\"COUNT\"\n"
				+ "							codeSystem=\"2.16.840.1.113883.5.84\"\n"
				+ "							codeSystemName=\"ObservationMethod\"\n"
				+ "							displayName=\"Count\"/>\n" + "					</observation>\n"
				+ "				</entryRelationship>\n" + "			</observation>\n" + "		</component>\n"
				+ "	</organizer>\n" + "</entry>";

		Context context = new Context();
		Node numeratorDenominatorNode = new QppXmlDecoder(context).decode(XmlUtils.stringToDom(xmlFragment));
		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(numeratorDenominatorNode.getChildNodes());

		String xPathExpected = "/*[local-name() = 'entry' and namespace-uri() = 'urn:hl7-org:v3']/*[local-name() = 'organizer' " +
		                       "and namespace-uri() = 'urn:hl7-org:v3']";

		QppOutputEncoder encoder = new QppOutputEncoder(context);
		List<Node> nodes = new ArrayList<>();
		nodes.add(numeratorDenominatorNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		String jsonExpected = "{\n  \"measureId\" : \"ACI-PEA-1\",\n  \"value\" : {\n    \"numerator\" : 600,\n    \"denominator\" : 800\n  }\n}";

		assertWithMessage("The XPath of the numerator denominator node is incorrect")
				.that(numeratorDenominatorNode.getChildNodes().get(0).getPath())
				.isEqualTo(xPathExpected);

		assertWithMessage("expected encoder to return a representation of a measure")
				.that(sw.toString())
				.isEqualTo(jsonExpected);
	}
}
