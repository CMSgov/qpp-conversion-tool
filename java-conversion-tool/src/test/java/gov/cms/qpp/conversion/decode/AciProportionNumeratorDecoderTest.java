package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class AciProportionNumeratorDecoderTest {

	@Before
	public void before() {
		Validations.init();
	}
	@After
	public void after() {
		Validations.clear();
	}

	@Test
	public void decodeACIProportionNumeratorAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.31\" extension=\"2016-09-01\" />\n"
				+ "		<!-- Numerator Count -->\n"
				+ "		<entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
				+ "			<qed resultName=\"aggregateCount\" resultValue=\"600\">\n"
				+ "				<templateId root=\"Q.E.D\"/>\n"
				+ "			</qed>"
				+ "		</entryRelationship>\n"
				+ "	</observation>\n"
				+ "</component>";

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));

		// For all Decoders this should be either a value or child node
		assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));
		// This is the child node that is produced by the intended Decoder
		Node aciProportionNumeratorNode = root.getChildNodes().get(0);
		// Should have a aggregate count node
		assertThat("returned node should have one child decoder node", aciProportionNumeratorNode.getChildNodes().size(),
				is(1));
		// This is the node with the numerator value
		Node numDenomNode = aciProportionNumeratorNode.getChildNodes().get(0);
		// Get the actual value
		String actual = (String) numDenomNode.getValue("aggregateCount");
		assertThat("aci numerator should be 600", actual, is("600"));

	}
	
	@Test
	public void decodeACIProportionNumeratorWithNoChildAsNode() throws Exception {
		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\" >\n"
				+ "	<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.31\" extension=\"2016-09-01\" />\n"
				+ "		<!-- Numerator Count **missing ** -->\n"
				+ "	</observation>\n"
				+ "</component>";

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));
		// We are missing the child node with the numerator count
		assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));
		// This is the child node that is produced by the intended Decoder
		Node aciProportionNumeratorNode = root.getChildNodes().get(0);
		// We are missing the child node with the numerator count
		assertThat("returned node should have one child decoder node", aciProportionNumeratorNode.getChildNodes().size(),is(0));

	}

}
