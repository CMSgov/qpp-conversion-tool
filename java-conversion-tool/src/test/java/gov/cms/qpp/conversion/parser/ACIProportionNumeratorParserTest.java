package gov.cms.qpp.conversion.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ACIProportionNumeratorParserTest {

	@Test
	public void parseACIProportionNumeratorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
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

		Element dom = XmlUtils.stringToDOM(xmlFragment);

		QppXmlInputParser parser = new QppXmlInputParser();
		parser.setDom(dom);

		Node victim = parser.parse();

		// This node is the place holder around the root node
		assertThat("returned node should not be null", victim, is(not(nullValue())));

		// For all parsers this should be either a value or child node
		assertThat("returned node should have one child node", victim.getChildNodes().size(), is(1));
		// This is the child node that is produced by the intended parser
		Node aciProportionNumeratorNode = victim.getChildNodes().get(0);
		// Should have a aggregate count node
		assertThat("returned node should have one child parser node", aciProportionNumeratorNode.getChildNodes().size(),
				is(1));
		// This is the node with the numerator value
		Node numDenomNode = aciProportionNumeratorNode.getChildNodes().get(0);
		// Get the actual value
		String actual = (String) numDenomNode.get("aciNumeratorDenominator");
		assertThat("aci numerator should be 600", actual, is("600"));

	}

}
