/*
 * Source code copyright Flexion
 * All rights reserved 
 */
package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class AggregateCountDecoderTest {

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
            + "        <entryRelationship typeCode=\"COMP\">\n"
            + "        <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
            + "                <value xsi:type=\"CD\" code=\"M\" codeSystem=\"2.16.840.1.113883.5.1\" codeSystemName=\"AdministrativeGenderCode\" displayName=\"Male\"/>\n"
            + "                <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n"
            + "                        <observation classCode=\"OBS\" moodCode=\"EVN\">\n"
            + "                                <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n"
            + "                                <templateId root=\"2.16.840.1.113883.10.20.27.3.16\" extension=\"2016-11-01\"/>\n" //Template Id should be ignored
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

    private static final String ANOTHER_XML_FRAGMENT = "<observation classCode=\"OBS\" moodCode=\"EVN\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
            + " Some extraneous text \n" // extraneous text element
            + "    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n"
            + "    <templateId root=\"R2.D2\"/>\n" // Funky templateId
            + "    <schmemplateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n" // Element that does not belong to the default namespace
            + "    <observation classCode=\"OBS\" moodCode=\"EVN\"> Empty observation </observation>\n" // Empty observation
            + "    <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>\n"
            + "    <statusCode code=\"completed\"/>\n"
            + " More extraneous text \n" // extraneous text element
            + "    <value xsi:type=\"INT\" value=\"400\"/>\n"
            + "    <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>\n"
            + "</observation>";

    @Test
    void testAggregateCountDecoderIgnoresInvalidTemplateId() throws Exception {
        Node parent = decodeAggregateCountFromXml(XML_FRAGMENT);
        Node measureData = parent.getChildNodes().get(0);

        assertThat(measureData.getChildNodes()).hasSize(1);
    }

    @Test
    void testAggregateCountDecoderHasValidAggregateCount() throws Exception {
        Node parent = decodeAggregateCountFromXml(XML_FRAGMENT);
        Node measureData = parent.getChildNodes().get(0);

        assertThat(measureData.getChildNodes().get(0).getType())
                .isEquivalentAccordingToCompareTo(TemplateId.ACI_AGGREGATE_COUNT);
    }

    @Test
    void testAggregateCountDecoderWithGarbageXmlDecodesValidValue() throws Exception {
        Node root = decodeAggregateCountFromXml(ANOTHER_XML_FRAGMENT);
        assertThat(root.getValue("aggregateCount")).isEqualTo("400");
    }

    @Test
    void testAggregateCountDecoderWithGarbageXmlDecodesValidType() throws Exception {
        Node root = decodeAggregateCountFromXml(ANOTHER_XML_FRAGMENT);
        assertThat(root.getType()).isEquivalentAccordingToCompareTo(TemplateId.ACI_AGGREGATE_COUNT);
    }

    private Node decodeAggregateCountFromXml(String xmlFragment) throws XmlException {
        Node root = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(xmlFragment));
        return root;
    }
}
