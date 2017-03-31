package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AciProportionMeasureDecoderTest {

	private static final String MEASURE_ID = "ACI-PEA-1";

	@Before
	public void before() {
		Validations.init();
	}

	@After
	public void after() {
		Validations.clear();
	}

	/**
	 * decodeACIProportionMeasureAsNode given a well formed xml fragment parses
	 * out the appropriate aggregateCount This test calls
	 * QppXmlDecoder.()decode() which in turn calls the only method in this
	 * class. AciProportionDenominatorDecoder().decode()
	 *
	 * @throws Exception
	 */
	@Test
	public void decodeACIProportionMeasureAsNode() throws Exception {
		String xmlFragment
				= "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<entry xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
				+ "	<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">\n"
				+ "		<!-- Implied template Measure Reference templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/>\n"
				+ "		<!-- ACI Numerator Denominator Type Measure Reference and Results templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.28\" extension=\"2016-09-01\"/>\n"
				+ "		<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>\n"
				+ "		<statusCode code=\"completed\"/>\n"
				+ "		<reference typeCode=\"REFR\">\n"
				+ "			<!-- Reference to a particular ACI measure's unique identifier. -->\n"
				+ "			<externalDocument classCode=\"DOC\" moodCode=\"EVN\">\n"
				+ "				<!-- This is a temporary root OID that indicates this is an ACI measure identifier -->\n"
				+ "				<!-- extension is the unique identifier for an ACI measure. \"ACI-PEA-1\" is for illustration only. -->\n"
				+ "				<id root=\"2.16.840.1.113883.3.7031\" extension=\"ACI-PEA-1\"/>\n"
				+ "				<!-- ACI measure title -->\n"
				+ "				<text>Patient Access</text>\n"
				+ "			</externalDocument>\n"
				+ "		</reference>\n"
				+ "		<component>\n"
				+ "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<!-- Performance Rate templateId -->\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.30\"\n"
				+ "					extension=\"2016-09-01\"/>\n"
				+ "				<code code=\"72510-1\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Performance Rate\"/>\n"
				+ "				<statusCode code=\"completed\"/>\n"
				+ "				<value xsi:type=\"REAL\" value=\"0.750000\"/>\n"
				+ "			</observation>\n"
				+ "		</component>\n"
				+ "		<component>\n"
				+ "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<!-- ACI Numerator Denominator Type Measure Numerator Data templateId -->\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.31\" extension=\"2016-09-01\"/>\n"
				+ "				<entryRelationship resultName=\"aciNumeratorDenominator\" resultValue=\"600\">\n"
				+ "					<templateId root=\"Q.E.D\"/>\n"
				+ "				</entryRelationship>"
				+ "			</observation>\n"
				+ "		</component>\n"
				+ "		<component>\n"
				+ "			<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
				+ "				<!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->\n"
				+ "				<templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\"/>\n"
				+ "				<entryRelationship resultName=\"aciNumeratorDenominator\" resultValue=\"800\">\n"
				+ "					<templateId root=\"Q.E.D\"/>\n"
				+ "				</entryRelationship>"
				+ "			</observation>\n"
				+ "		</component>\n"
				+ "	</organizer>\n"
				+ "</entry>";

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
		// remove default nodes (will fail if defaults change)
		DefaultDecoder.removeDefaultNode(root.getChildNodes());

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));

		// For all decoders this should be either a value or child node
		assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));
		// This is the child node that is produced by the intended decoder
		Node aciProportionMeasureNode = root.getChildNodes().get(0);
		// Should have a aggregate count node 
		assertThat("returned node should have two child decoder nodes", aciProportionMeasureNode.getChildNodes().size(), is(2));

		assertThat("measureId should be " + MEASURE_ID,
				aciProportionMeasureNode.getValue("measureId"), is(MEASURE_ID));

		List<String> testTemplateIds = new ArrayList<>();
		for (Node node : aciProportionMeasureNode.getChildNodes()) {
			testTemplateIds.add(node.getId());
		}

		assertThat("Should have Numerator", testTemplateIds.contains("2.16.840.1.113883.10.20.27.3.31"), is(true));
		assertThat("Should have Denominator", testTemplateIds.contains("2.16.840.1.113883.10.20.27.3.32"), is(true));
	}

	@Test
	public void decodeACIProportionMeasureAsMissingElementsNode() throws Exception {
		String xmlFragment
				= "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<entry xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\" >\n"
				+ "	<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">\n"
				+ "		<!-- ACI Numerator Denominator Type Measure Reference and Results templateId -->\n"
				+ "		<templateId root=\"2.16.840.1.113883.10.20.27.3.28\" extension=\"2016-09-01\"/>\n"
				+ "		<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>\n"
				+ "		<statusCode code=\"completed\"/>\n"
				+ "		<reference typeCode=\"REFR\">\n"
				+ "			<!-- Reference to a particular ACI measure's unique identifier. *** missing *** -->\n"
				+ "		</reference>\n"
				+ "	</organizer>\n"
				+ "</entry>";

		Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

		// This node is the place holder around the root node
		assertThat("returned node should not be null", root, is(not(nullValue())));

		// For all decoders this should be either a value or child node
		assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));
		// This is the child node that is produced by the intended decoder
		Node aciProportionMeasureNode = root.getChildNodes().get(0);
		// We have no component nodes
		assertThat("returned node should have no child decoder nodes", aciProportionMeasureNode.getChildNodes().size(), is(0));
		// The measureId in not reachable
		assertThat("measureId should be null", aciProportionMeasureNode.getValue("measureId"), is(nullValue()));
	}

	@Test
	public void testInternalDecode() throws Exception {
		//set-up
		Namespace rootns = Namespace.getNamespace("urn:hl7-org:v3");
		Namespace ns = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element element = new Element("organizer", rootns);
		Element templateIdElement = new Element("templateId", rootns)
			                            .setAttribute("root","2.16.840.1.113883.10.20.27.3.28");
		Element referenceElement = new Element("reference", rootns);
		Element externalDocumentElement = new Element("externalDocument", rootns);
		Element idElement = new Element("id", rootns).setAttribute("extension", MEASURE_ID);

		externalDocumentElement.addContent(idElement);
		referenceElement.addContent(externalDocumentElement);
		element.addContent(templateIdElement);
		element.addContent(referenceElement);
		element.addNamespaceDeclaration(ns);

		Node thisNode = new Node();

		AciProportionMeasureDecoder objectUnderTest = new AciProportionMeasureDecoder();
		objectUnderTest.setNamespace(element, objectUnderTest);

		//execute
		objectUnderTest.internalDecode(element, thisNode);

		//assert
		assertThat("measureId should be " + MEASURE_ID, thisNode.getValue("measureId"), is(MEASURE_ID));
	}
}
