package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ACIProportionDenominatorDecoderTest {

	@Test
	public void decodeACIProportionNumeratorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
				+ "	<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "		<!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\" />\n"
				+ "		<code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\" />\n"
				+ "		<statusCode code=\"completed\" />\n"
				+ "		<value xsi:type=\"CD\" code=\"DENOM\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" />\n"
				+ "		<!-- Denominator Count -->\n"
				+ "		<entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "			<observation resultName=\"aciNumeratorDenominator\" resultValue=\"600\">\n"
				+ "				<templateId root=\"Q.E.D\"/>\n"
				+ "			</observation>"
				+ "		</entryRelationship>\n" 
				+ "	</observation>\n" 
				+ "</component>";

		Element dom = XmlUtils.stringToDOM(xmlFragment);

		QppXmlDecoder decoder = new QppXmlDecoder();
		decoder.setDom(dom);

		Node root = decoder.decode();

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));

		// For all decoders this should be either a value or child node
		assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));
		// This is the child node that is produced by the intended decoder
		Node aciProportionDenominatorNode = root.getChildNodes().get(0);
		// Should have a aggregate count node
		assertThat("returned node should have one child decoder node",
				aciProportionDenominatorNode.getChildNodes().size(), is(1));
		// This is stubbed node with the test value
		Node target = aciProportionDenominatorNode.getChildNodes().get(0);
		// Get the test value
		assertThat("test value should be mytestvalue", target.get("aciNumeratorDenominator"), is("600"));

	}

}
