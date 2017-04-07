/*
 * Source code copyright Flexion
 * All rights reserved 
 */
package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import org.jdom2.Element;
import org.jdom2.Namespace;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class AggregateCountDecoderTest {

    private static final String XML_FRAGMENT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<entry xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
            + "<component>\n"
            + "<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
            + "        <templateId root=\"2.16.840.1.113883.10.20.27.3.5\" extension=\"2016-09-01\"/>\n"
            + "        <templateId root=\"2.16.840.1.113883.10.20.27.3.16\" extension=\"2016-11-01\"/>\n"
            + "        <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>\n"
            + "        <statusCode code=\"completed\"/>\n"
            + "        <value xsi:type=\"CD\" code=\"DENOM\" codeSystem=\"2.16.840.1.113883.5.1063\" codeSystemName=\"ObservationValue\"/>\n"
            + "        <!--DENOM Count-->\n"
            + "       <entryRelationship typeCode=\"COMP\">\n"
            + "        <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
            + "                <value xsi:type=\"CD\" code=\"M\" codeSystem=\"2.16.840.1.113883.5.1\" codeSystemName=\"AdministrativeGenderCode\" displayName=\"Male\"/>\n"
            + "                <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
            + "                        <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
            + "                                <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n"
            + "                                <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>\n"
            + "                                <statusCode code=\"completed\"/>\n"
            + "                                <value xsi:type=\"INT\" value=\"400\"/>\n"
            + "                                <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>\n"
            + "                        </observation>\n"
            + "                </entryRelationship>\n"
            + "        </observation>\n"
            + "</entryRelationship>\n"
            + "</observation>\n"
            + "</component>"
            + "</entry>";

    @Test
    public void testInternalDecode() throws Exception {
        Namespace rootNs = Namespace.getNamespace("urn:hl7-org:v3");
        Namespace ns = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        Element element = new Element("observation", rootNs);
        element.addContent(new Element("templateId", rootNs).setAttribute("root", "2.16.840.1.113883.10.20.27.3.3"));
        element.addContent(new Element("value", rootNs).setAttribute("value", "450").setAttribute("type", "INT", ns));
        element.addNamespaceDeclaration(ns);

        Node thisNode = new Node();

        AggregateCountDecoder instance = new AggregateCountDecoder();
        instance.setNamespace(element, instance);

        instance.internalDecode(element, thisNode);

        assertThat("Aggregate Count should be 450 ", thisNode.getValue("aggregateCount"), is("450"));

    }

    @Test
    public void testAggregateCount() throws Exception {

        Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(XML_FRAGMENT));
        Node node = root.getChildNodes().get(0);

        assertThat("returned node should not be null", root, is(not(nullValue())));

        assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));

        assertThat("returned node should have one child decoder nodes", node.getChildNodes().size(), is(1));

        assertThat("DefaultDecoderFor should be Measure Data - CMS (V2)",
                node.getValue("DefaultDecoderFor"), is("Measure Data - CMS (V2)"));

        assertThat("Should have template id", node.getChildNodes().get(0).getId(), is("2.16.840.1.113883.10.20.27.3.3"));
    }

    @Test
    public void testAggregateCountDecoderIgnoresInvalidElements() throws XmlException {
        String garbageXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<entry xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
                + "<component>\n"
                + "<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
                + "        <templateId root=\"2.16.840.1.113883.10.20.27.3.5\" extension=\"2016-09-01\"/>\n"
                + "        <templateId root=\"2.16.840.1.113883.10.20.27.3.16\" extension=\"2016-11-01\"/>\n"
                + "        <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>\n"
                + "        <statusCode code=\"completed\"/>\n"
                + "        <value xsi:type=\"CD\" code=\"DENOM\" codeSystem=\"2.16.840.1.113883.5.1063\" codeSystemName=\"ObservationValue\"/>\n"
                + "        <!--DENOM Count-->\n"
                + "       <entryRelationship typeCode=\"COMP\">\n"
                + "        <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
                + "                <value xsi:type=\"CD\" code=\"M\" codeSystem=\"2.16.840.1.113883.5.1\" codeSystemName=\"AdministrativeGenderCode\" displayName=\"Male\"/>\n"
                + "                <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
                + "                        <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
                + "                                <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n"
                + "                                <whatsThis code=\"completed\"/>\n"
                + "                                <notCorrect xsi:type=\"STRING\" value=\"FALSE\"/>\n"
                + "                                <methodCode code=\"1234213131\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"test\" displayName=\"Count\"/>\n"
                + "                                <abc xsi:type=\"INT\" value=\"123\"/>\n"
                + "                                <testingNoValue />"
                + "                        </observation>\n"
                + "                </entryRelationship>\n"
                + "        </observation>\n"
                + "</entryRelationship>\n"
                + "</observation>\n"
                + "</component>"
                + "</entry>";

        Node nodeUnderTest = new QppXmlDecoder().decode(XmlUtils.stringToDOM(garbageXml));

        assertThat("Parse no value from garbage", nodeUnderTest.getChildNodes().get(0).getValue("aggregateCount"), is(nullValue()));
    }
}
