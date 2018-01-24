package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * AciProportionDenominatorDecoderTest JUnit test for
 * AciProportionDenominatorDecoder
 */
class AciProportionDenominatorDecoderTest {

	/**
	 * decodeACIProportionDenominatorAsNode given a well formed xml fragment
	 * parses out the appropriate aggregateCount This test calls
	 * QrdaXmlDecoder.()decode() which in turn calls the only method in this
	 * class. AciProportionDenominatorDecoder().decode()
	 *
	 * @throws Exception
	 */
	@Test
	void decodeACIProportionDenominatorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ " <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "     <!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->\n"
				+ "     <templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\" />\n"
				+ "     <!-- Denominator Count -->\n"
				+ "     <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "         <qed resultName=\"aggregateCount\" resultValue=\"800\">\n"
				+ "             <templateId root=\"Q.E.D\"/>\n"
				+ "         </qed>"
				+ "     </entryRelationship>\n"
				+ " </observation>\n"
				+ "</component>";
		Node root = new QrdaXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		// For all decoders this should be either a value or child node
		assertThat(root.getChildNodes())
				.hasSize(1);

		// This is the child node that is produced by the intended decoder
		Node aciProportionDenominatorNode = root.getChildNodes().get(0);
		// Should have a aggregate count node
		assertThat(aciProportionDenominatorNode.getChildNodes())
				.hasSize(1);
		// This is stubbed node with the test value
		Node target = aciProportionDenominatorNode.getChildNodes().get(0);
		// Get the test value
		assertThat(target.getValue("aggregateCount"))
				.isEqualTo("800");
	}

	@Test
	void decodeInvalidACIProportionDenominatorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
			+ " <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
			+ "     <!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->\n"
			+ "     <templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\" />\n"
			+ "     <!-- Denominator Count -->\n"
			+ "     <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
			+ "         <qed resultName=\"aggregateCount\" resultValue=\"800\">\n"
			+ "             <templateId root=\"Q.E.D\"/>\n"
			+ "         </qed>"
			+ "     </entryRelationship>\n"
			+ " </observation>\n"
			+ "  <observation classCode=\"OBS\" moodCode=\"EVN\">"
			+ "    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>"
			+ "    <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>"
			+ "    <statusCode code=\"completed\"/>"
		    + "    <value xsi:type=\"INT\" value=\"600\"/>"
			+ "    <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>"
			+ "  </observation>"
			+ " </component>";
		Node root = new QrdaXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));

		// For all decoders this should be either a value or child node
		assertThat(root.getChildNodes()).hasSize(2);
		// This is the child node that is produced by the intended decoder
		Node aciProportionDenominatorNode = root.getChildNodes().get(0);
		// Should have a aggregate count node
		assertThat(aciProportionDenominatorNode.getChildNodes()).hasSize(1);
		// This is stubbed node with the test value
		Node target = aciProportionDenominatorNode.getChildNodes().get(0);
		// Get the test value
		assertThat(target.getValue("aggregateCount"))
				.isEqualTo("800");
	}
}
