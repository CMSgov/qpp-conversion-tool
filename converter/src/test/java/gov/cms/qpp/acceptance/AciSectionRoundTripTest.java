package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class AciSectionRoundTripTest {

	@Test
	void parseSparseAciSectionAsNode() throws XmlException {
		//set-up
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
							 + "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
							 + "	<section>\n" + "		<!-- Measure Section -->\n"
							 + "		<templateId root=\"2.16.840.1.113883.10.20.24.2.2\"/>\n"
							 + "		<!-- Advancing Care Information Section templateId -->\n"
							 + "		<templateId root=\"2.16.840.1.113883.10.20.27.2.5\" extension=\"2017-06-01\"/>\n"
							 + "		<code code=\"55186-1\" codeSystem=\"2.16.840.1.113883.6.1\" displayName=\"Measure Section\"/>\n"
							 + "		<title>Measure Section</title>\n" + "		<text>\n" + "		</text>\n"
							 + "		<entry>\n"
							 + "			<qed resultName=\"measure\" resultValue=\"measure1\">\n"
							 + "				<templateId root=\"Q.E.D\"/>\n"
							 + "			</qed>"
							 + "		</entry>\n"
							 + "		<entry typeCode=\"DRIV\">"
							 + "			<act classCode=\"ACT\" moodCode=\"EVN\">"
							 + "				<templateId root=\"2.16.840.1.113883.10.20.17.3.8\"/>"
							 + "				<id root=\"00b669fd-fa4d-4f5c-b109-65c6bbbf73ae\"/>"
							 + "				<code code=\"252116004\" codeSystem=\"2.16.840.1.113883.6.96\""
							 + "					displayName=\"Observation Parameters\"/>"
							 + "				<effectiveTime>"
							 + "					<low value=\"20170101\"/>"
							 + "					<high value=\"20170430\"/>"
							 + "				</effectiveTime>"
							 + "			</act>"
							 + "		</entry>"
							 + "	</section>\n"
							 + "</component>";

		//execute
		Node parentNode = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		//assert
		Node aciSectionNode = parentNode.findFirstNode(TemplateId.PI_SECTION);
		assertAciSectionHasSingleQedNode(aciSectionNode);
	}

	@Test
	void parseGarbageAciSectionAsNode() throws XmlException {
		//set-up
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
							+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
							 + "	<section>\n" + "		<!-- Measure Section -->\n"
							 + "		<templateId root=\"2.16.840.1.113883.10.20.24.2.2\"/>\n"
							 + "		<!-- Advancing Care Information Section templateId -->\n"
							 + "		<templateId root=\"2.16.840.1.113883.10.20.27.2.5\" extension=\"2017-06-01\"/>\n"
							 + "		<code code=\"55186-1\" codeSystem=\"2.16.840.1.113883.6.1\" displayName=\"Measure Section\"/>\n"
							 + "        <statusCode code=\"Death and Destruction!\" />\n"
							 + "        Utter garbage!  Buahahahahaha!\n"
							 + "		<title>Measure Section</title>\n" + "		<text>\n" + "		</text>\n"
							 + "		<entry>\n"
							 + "			<qed resultName=\"measure\" resultValue=\"measure1\">\n"
							 + "				<templateId root=\"Q.E.D\"/>\n"
							 + "			</qed>"
							 + "		</entry>\n"
							 + "		<entry typeCode=\"DRIV\">"
							 + "			<act classCode=\"ACT\" moodCode=\"EVN\">"
							 + "				<templateId root=\"2.16.840.1.113883.10.20.17.3.8\"/>"
							 + "				<id root=\"00b669fd-fa4d-4f5c-b109-65c6bbbf73ae\"/>"
							 + "				<code code=\"252116004\" codeSystem=\"2.16.840.1.113883.6.96\""
						 	 + "					displayName=\"Observation Parameters\"/>"
							 + "				<effectiveTime>"
							 + "					<low value=\"20170101\"/>"
							 + "					<high value=\"20170430\"/>"
							 + "				</effectiveTime>"
							 + "			</act>"
							 + "		</entry>"
							 + "	</section>\n"
							 + "</component>";

		//execute
		Node parentNode = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		//assert
		Node aciSectionNode = parentNode.findFirstNode(TemplateId.PI_SECTION);
		assertAciSectionHasSingleQedNode(aciSectionNode);
	}

	@Test
	void parseAciSectionAsJson() throws EncodeException, XmlException {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<section>\n" + "		<!-- Measure Section -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.24.2.2\"/>\n"
				+ "		<!-- Advancing Care Information Section templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.2.5\" extension=\"2017-06-01\"/>\n"
				+ "		<code code=\"55186-1\" codeSystem=\"2.16.840.1.113883.6.1\" displayName=\"Measure Section\"/>\n"
				+ "		<title>Measure Section</title>\n" + "		<text>\n" + "		</text>\n" + "		<entry>\n"
				+ "			<qed resultName=\"measure\" resultValue=\"measure1\">\n"
				+ "				<templateId root=\"Q.E.D\"/>\n"
				+ "			</qed>"
				+ "		</entry>\n"
				+ "		<entry typeCode=\"DRIV\">"
				+ "			<act classCode=\"ACT\" moodCode=\"EVN\">"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.17.3.8\"/>"
				+ "				<id root=\"00b669fd-fa4d-4f5c-b109-65c6bbbf73ae\"/>"
				+ "				<code code=\"252116004\" codeSystem=\"2.16.840.1.113883.6.96\""
				+ "					displayName=\"Observation Parameters\"/>"
				+ "				<effectiveTime>"
				+ "					<low value=\"20170101\"/>"
				+ "					<high value=\"20170430\"/>"
				+ "				</effectiveTime>"
				+ "			</act>"
				+ "		</entry>"
				+ "	</section>\n"
				+ "</component>";

		String expected = "{\n  \"category\" : \"pi\",\n  \"submissionMethod\" : \"electronicHealthRecord\",\n  \"measurements\" : [ {\n    \"measure\" : \"measure1\"\n  } ],\n  \"performanceStart\" : \"2017-01-01\",\n  \"performanceEnd\" : \"2017-04-30\"\n}";

		Context context = new Context();
		//Decode
		Node measureNode = new QrdaDecoderEngine(context).decode(XmlUtils.stringToDom(xmlFragment));
		// remove default nodes (will fail if defaults change)

		//Encode
		QppOutputEncoder encoder = new QppOutputEncoder(context);
		List<Node> nodes = new ArrayList<>();
		nodes.add(measureNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw), true);

		//Test
		assertThat(sw.toString()).isEqualTo(expected);
	}

	private void assertAciSectionHasSingleQedNode(Node aciSectionNode) {
		assertThat(aciSectionNode).isNotNull();
		assertThat(aciSectionNode.getChildNodes().get(0).getType())
				.isEquivalentAccordingToCompareTo(TemplateId.QED);
	}
}
