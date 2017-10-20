package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class AciProportionDenominatorDecoderTest {

	@Test
	public void decodeInvalidACIProportionDenominatorAsNode() throws XmlException {
		Node aciProportionDenominatorNode = getValidAciProportionDenominatorNode();
		String actual = aciProportionDenominatorNode.getValue("name");
		String expected = "aciProportionDenominator";

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testAciProportionDenominatorDecoderContainsAnAggregateCount() throws XmlException {
		Node aciProportionDenominatorNode = getValidAciProportionDenominatorNode();
		Node aggregateCount = aciProportionDenominatorNode.getChildNodes().get(0);
		
		assertThat(aggregateCount.getType()).isEquivalentAccordingToCompareTo(TemplateId.ACI_AGGREGATE_COUNT);
	}

	private Node getValidAciProportionDenominatorNode() throws XmlException {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ " <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "     <!-- ACI_DENOMINATOR Data templateId -->\n"
				+ "     <templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\" />\n"
				+ " <!-- Denominator Count -->\n"
				+ "  <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "   <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "     <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n"
				+ "     <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>\n"
				+ "     <statusCode code=\"completed\"/>\n"
				+ "     <value xsi:type=\"INT\" value=\"600\"/>\n"
				+ "     <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>\n"
				+ "   </observation>\n"
				+ "  </entryRelationship>\n"
				+ " </observation>\n"
				+ " </component>";
		Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
		return root.getChildNodes().get(0);
	}
}
