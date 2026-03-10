package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class AciProportionDenominatorRoundTripTest {

	@Test
	void parseAciNumeratorDenominatorAsNode() throws Exception {
		String xmlFragment = """
				<?xml version="1.0" encoding="utf-8"?>
				<component xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:hl7-org:v3">
					<observation classCode="OBS" moodCode="EVN">
						<!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->
						<templateId root="2.16.840.1.113883.10.20.27.3.32" extension="2016-09-01" />
						<code code="ASSERTION" codeSystem="2.16.840.1.113883.5.4" codeSystemName="ActCode" displayName="Assertion" />
						<statusCode code="completed" />
						<value xsi:type="CD" code="DENOM" codeSystem="2.16.840.1.113883.5.4" codeSystemName="ActCode" />
						<!-- Denominator Count -->
						<entryRelationship typeCode="SUBJ" inversionInd="true">
							<observation classCode="OBS" moodCode="EVN">
								<templateId root="2.16.840.1.113883.10.20.27.3.3" />
								<code code="MSRAGG" codeSystem="2.16.840.1.113883.5.4" codeSystemName="ActCode" displayName="rate aggregation" />
								<statusCode code="completed" />
								<value xsi:type="INT" value="600" />
								<methodCode code="COUNT" codeSystem="2.16.840.1.113883.5.84" codeSystemName="ObservationMethod" displayName="Count" />
							</observation>\
						</entryRelationship>
					</observation>
				</component>""";

		Context context = new Context();
		Node numDenomNode = new QrdaDecoderEngine(context).decode(XmlUtils.stringToDom(xmlFragment));

		QppOutputEncoder encoder = new QppOutputEncoder(context);
		List<Node> nodes = new ArrayList<>();
		nodes.add(numDenomNode);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw), true);

		String EXPECTED = "{\n  \"denominator\" : 600\n}";
		assertThat(sw.toString())
				.isEqualTo(EXPECTED);
	}
}
