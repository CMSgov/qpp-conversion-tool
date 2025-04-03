package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.util.JsonHelper;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.TypeRef;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class PiSectionRoundTripTest {

	private static final Path PI_RESTRICTED_MEASURES =
		Path.of("src/test/resources/negative/mipsInvalidPIMeasureIds.xml");
	private static final Path APP1_APM_ENTITY_CEHRT =
			Path.of("src/test/resources/app/2024/App1-ApmEntity-Qrda-III.xml");
	private static final Path APP1_GROUP_CEHRT =
		Path.of("src/test/resources/app/2024/App1-Group-QRDA-III.xml");
	private static final Path APP1_INDIVIDUAL_CEHRT =
		Path.of("src/test/resources/app/2024/App1-Indv-QRDA-III.xml");

	@Test
	void parseSparsePiSectionAsNode() throws XmlException {
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
		Node aciSectionNode = parentNode.findFirstNode(TemplateId.PI_SECTION_V3);
		assertAciSectionHasSingleQedNode(aciSectionNode);
	}

	@Test
	void parseGarbagePiSectionAsNode() throws XmlException {
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
		Node aciSectionNode = parentNode.findFirstNode(TemplateId.PI_SECTION_V3);
		assertAciSectionHasSingleQedNode(aciSectionNode);
	}

	@Test
	void parsePiSectionAsJson() throws EncodeException, XmlException {
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

	@Test
	void testPiSectionRestrictedMeasures() {
		Converter converter = new Converter(new PathSource(PI_RESTRICTED_MEASURES));

		List<Detail> details = new ArrayList<>();
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.PI_RESTRICTED_MEASURES);
	}

	@Test
	void testAppApmCehrtIsEncoded() {
		Converter converter = new Converter(new PathSource(APP1_APM_ENTITY_CEHRT));
		AllErrors errors = null;
		List<Detail> warnings = null;
		JsonWrapper qppWrapper = null;


		List<Detail> details = new ArrayList<>();
		try {
			qppWrapper = converter.transform();
		} catch (TransformException failure) {
			errors = failure.getDetails();
			warnings = failure.getConversionReport().getWarnings();
		}

		assertThat(errors).isNull();
		assertThat(warnings).isNull();
		List<String> cehrtIdList = JsonHelper.readJsonAtJsonPath(qppWrapper.toString(),
			"$.measurementSets[?(@.category=='pi')].cehrtId", new TypeRef<List<String>>() { });
		assertThat(cehrtIdList.get(0)).isEqualTo("XX15CXXXXXXXXXX");
	}

	@Test
	void testAppGroupCehrtIsEncoded() {
		Converter converter = new Converter(new PathSource(APP1_GROUP_CEHRT));
		AllErrors errors = null;
		List<Detail> warnings = null;
		JsonWrapper qppWrapper = null;


		List<Detail> details = new ArrayList<>();
		try {
			qppWrapper = converter.transform();
		} catch (TransformException failure) {
			errors = failure.getDetails();
			warnings = failure.getConversionReport().getWarnings();
		}

		assertThat(errors).isNull();
		assertThat(warnings).isNull();
		List<String> cehrtIdList = JsonHelper.readJsonAtJsonPath(qppWrapper.toString(),
			"$.measurementSets[?(@.category=='pi')].cehrtId", new TypeRef<List<String>>() { });
		assertThat(cehrtIdList.get(0)).isEqualTo("XX15CXXXXXXXXXX");
	}

	@Test
	void testAppIndividualCehrtIsEncoded() {
		Converter converter = new Converter(new PathSource(APP1_INDIVIDUAL_CEHRT));
		AllErrors errors = null;
		List<Detail> warnings = null;
		JsonWrapper qppWrapper = null;


		List<Detail> details = new ArrayList<>();
		try {
			qppWrapper = converter.transform();
		} catch (TransformException failure) {
			errors = failure.getDetails();
			warnings = failure.getConversionReport().getWarnings();
		}

		assertThat(errors).isNull();
		assertThat(warnings).isNull();
		List<String> cehrtIdList = JsonHelper.readJsonAtJsonPath(qppWrapper.toString(),
			"$.measurementSets[?(@.category=='pi')].cehrtId", new TypeRef<List<String>>() { });
		assertThat(cehrtIdList.get(0)).isEqualTo("XX15CXXXXXXXXXX");
	}

	private void assertAciSectionHasSingleQedNode(Node aciSectionNode) {
		assertThat(aciSectionNode).isNotNull();
		assertThat(aciSectionNode.getChildNodes().get(0).getType())
				.isEquivalentAccordingToCompareTo(TemplateId.QED);
	}
}
