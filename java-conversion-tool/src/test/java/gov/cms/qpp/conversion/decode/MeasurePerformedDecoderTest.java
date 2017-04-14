package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class MeasurePerformedDecoderTest {

	@Test
	public void testMeasurePerformed() throws XmlException {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "<observation classCode=\"OBS\" moodCode=\"EVN\">"
				+ "        <!-- Measure Performed templateId -->"
				+ "        <templateId root=\"2.16.840.1.113883.10.20.27.3.27\" extension=\"2016-09-01\"/>"
				+ "        <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>"
				+ "        <statusCode code=\"completed\"/>"
				+ "        <value xsi:type=\"CD\" code=\"Y\" displayName=\"Yes\" codeSystemName=\"Yes/no indicator (HL7 Table 0136)\" codeSystem=\"2.16.840.1.113883.12.136\"/>"
				+ "    </observation>"
				+ "</component>";

		Node measurePerformedNode = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		assertThat("Should have a measure perform",
				measurePerformedNode.getChildNodes().get(0).getValue("measurePerformed"), is("Y"));

		assertThat("Should have one child", measurePerformedNode.getChildNodes(), hasSize(1));
	}
}
