package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class AciNumeratorDenominatorDecoderTest {

    @Test
    public void decodeRateAggregationAsNode() throws Exception {
        String xmlFragment = XmlUtils.buildString(
                "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
                "  <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
                "    <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
                "    <statusCode code=\"completed\"/>", "    <value xsi:type=\"INT\" value=\"600\"/>",
                "    <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
                "  </observation>",
                "</root>");

        Node rateAggreagationNode = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

        // the returned Node object from the snippet should be:
        // a top level placeholder node with a single child node that has the
        // "rateAggregationDenominator" key in it

        assertThat("returned node should not be null", rateAggreagationNode, is(not(nullValue())));

        assertThat("returned node should have one child node", rateAggreagationNode.getChildNodes().size(), is(1));

        assertThat("aci rate aggregation value should be 600",
                rateAggreagationNode.getChildNodes().get(0).getValue("aggregateCount"), is("600"));
    }

    @Test
    public void decodeAciNumeratorDenominatorNullValueAsNode() throws Exception {
        String xmlFragment = XmlUtils.buildString(
                "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\" >",
                "  <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
                "    <value xsi:type=\"INT\"/>",
                "  </observation>",
                "</root>");

        Node numDenomNode = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

        // the returned Node object from the snippet should be:
        // a top level placeholder node with a single child node that has the
        // "rateAggregationDenominator" key in it

        assertThat("returned node should not be null", numDenomNode, is(not(nullValue())));

        assertThat("returned node should have one child node", numDenomNode.getChildNodes().size(), is(1));

        assertThat("aci numerator/denominator value should be null", numDenomNode.getChildNodes().get(0).getValue("aggregateCount"), is(nullValue()));
    }

    @Test
    public void decodeAciNumeratorDenominatorNullElementAsNode() throws Exception {
        String xmlFragment = XmlUtils.buildString(
                "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
                "  <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
                "  </observation>",
                "</root>");

        Node numDenomNode = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));

        // the returned Node object from the snippet should be:
        // a top level placeholder node with a single child node that has the
        // "rateAggregationDenominator" key in it

        assertThat("returned node should not be null", numDenomNode, is(not(nullValue())));

        assertThat("returned node should have one child node", numDenomNode.getChildNodes().size(), is(1));

        assertThat("aci numerator/denominator value should be null", numDenomNode.getChildNodes().get(0).getValue("aggregateCount"), is(nullValue()));
    }

    @Test
    public void decodeAciNumeratorDenominatorValidTest() throws XmlException {
        Node aciMeasureNode = new QppXmlDecoder().decode(XmlUtils.stringToDOM(getValidXmlFragment()));
        assertThat("Decoded xml fragment should contain some nodes",
                aciMeasureNode.getChildNodes().size(), is(2));

        List<Node> nodeList = aciMeasureNode.findNode(TemplateId.ACI_NUMERATOR.getTemplateId());
        assertThat("Decoded xml fragment should contain " + TemplateId.ACI_NUMERATOR.name(),
                nodeList.size(), is(1));

        nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
        assertThat("Decoded xml fragment " + TemplateId.ACI_NUMERATOR.name() +
                        " should contain " + TemplateId.ACI_AGGREGATE_COUNT.name(),
                nodeList.size(), is(1));

        nodeList = aciMeasureNode.findNode(TemplateId.ACI_DENOMINATOR.getTemplateId());
        assertThat("Decoded xml fragment should contain " + TemplateId.ACI_DENOMINATOR.name(),
                nodeList.size(), is(1));

        nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
        assertThat("Decoded xml fragment " + TemplateId.ACI_NUMERATOR.name() +
                        " should contain " + TemplateId.ACI_AGGREGATE_COUNT.name(),
                nodeList.size(), is(1));
        int numberNodes = countNodes(aciMeasureNode);
        assertThat("Decoded xml fragment " + aciMeasureNode.getType().name() +
                        " should contain 8 nodes", numberNodes, is(8));
    }

    @Test
    public void decodeAciNumeratorDenominatorExtraneousXMLTest() throws XmlException {
        String xmlFragment = getValidXmlFragment();
        xmlFragment = xmlFragment.replaceAll("<statusCode ",
        "\n<Stuff arbitrary=\"123\"><newnode>Some extra stuff</newnode></Stuff>Unexpected stuff appears here\n\n<statusCode ");

        Node aciMeasureNode = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
        assertThat("Decoded xml fragment should contain some nodes",
                aciMeasureNode.getChildNodes().size(), is(2));

        List<Node> nodeList = aciMeasureNode.findNode(TemplateId.ACI_NUMERATOR.getTemplateId());
        assertThat("Decoded xml fragment should contain " + TemplateId.ACI_NUMERATOR.name(),
                nodeList.size(), is(1));

        nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
        assertThat("Decoded xml fragment " + TemplateId.ACI_NUMERATOR.name() +
                        " should contain " + TemplateId.ACI_AGGREGATE_COUNT.name(),
                nodeList.size(), is(1));

        nodeList = aciMeasureNode.findNode(TemplateId.ACI_DENOMINATOR.getTemplateId());
        assertThat("Decoded xml fragment should contain " + TemplateId.ACI_DENOMINATOR.name(),
                nodeList.size(), is(1));

        nodeList = nodeList.get(0).findNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
        assertThat("Decoded xml fragment " + TemplateId.ACI_NUMERATOR.name() +
                        " should contain " + TemplateId.ACI_AGGREGATE_COUNT.name(),
                nodeList.size(), is(1));
        int numberNodes = countNodes(aciMeasureNode);
        assertThat("Decoded xml fragment " + aciMeasureNode.getType().name() +
                " should contain 7 nodes", numberNodes, is(8));
        System.out.println(aciMeasureNode.toString()+"\n");
	    System.out.println(xmlFragment);
    }

    private int countNodes(Node parent){
        int count = 1;
        if ( parent == null ) {
            return count;
        }
        List<Node> children = parent.getChildNodes();
        if ( children == null || children.isEmpty()){
            return count;
        }
        for (Node node : children ){
            count += countChildren(node);
        }
        return count;
    }

    private int countChildren(Node parent){
        int count = 1;
        if ( parent == null ) {
            return count;
        }
        List<Node> children = parent.getChildNodes();
        if ( children == null || children.isEmpty()){
            return count;
        }
        for (Node node : children ){
            count += countNodes(node);
        }
        return count;
    }

    private String getValidXmlFragment(){
        String xmlFragment = XmlUtils.buildString(
                "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">",
                "\n<entry>",
                "\n<organizer classCode=\"CLUSTER\" moodCode=\"EVN\">",
                "\n<!-- Implied template Measure Reference templateId -->",
                "\n<templateId root=\"2.16.840.1.113883.10.20.27.2.5\" extension=\"2016-09-01\"/>",
                "\n<!-- ACI Numerator Denominator Type Measure Reference and Results templateId -->",
                "\n<templateId root=\"2.16.840.1.113883.10.20.27.3.28\" extension=\"2016-09-01\"/>",
                "\n<id root=\"ac575aef-7062-4ea2-b723-df517cfa470a\"/>",
                "\n<statusCode code=\"completed\"/>",
                "\n<reference typeCode=\"REFR\">",
                "\n <!-- Reference to a particular ACI measure's unique identifier. -->",
                "\n <externalDocument classCode=\"DOC\" moodCode=\"EVN\">",
                "\n     <!-- This is a temporary root OID that indicates this is an ACI measure identifier -->",
                "\n     <!-- extension is the unique identifier for an ACI measure. \"ACI-PEA-1\" is for illustration only. -->",
                "\n     <id root=\"2.16.840.1.113883.3.7031\" extension=\"ACI-PEA-1\"/>",
                "\n     <!-- ACI measure title -->",
                "\n     <text>Patient Access</text>",
                "\n </externalDocument>",
                "\n</reference>",
                "\n<component>",
                "\n <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "\n     <!-- Performance Rate templateId -->",
                "\n     <templateId root=\"2.16.840.1.113883.10.20.27.3.30\" extension=\"2016-09-01\"/>",
                "\n     <code code=\"72510-1\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Performance Rate\"/>",
                "\n     <statusCode code=\"completed\"/>",
                "\n     <value xsi:type=\"REAL\" value=\"0.750000\"/>",
                "\n </observation>",
                "\n</component>",
                "\n<component>",
                "\n <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "\n     <!-- ACI Numerator Denominator Type Measure Numerator Data templateId -->",
                "\n     <templateId root=\"2.16.840.1.113883.10.20.27.3.31\" extension=\"2016-09-01\"/>",
                "\n     <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>",
                "\n     <statusCode code=\"completed\"/>",
                "\n     <value xsi:type=\"CD\" code=\"NUMER\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\"/>",
                "\n     <!-- Numerator Count-->",
                "\n     <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">",
                "\n         <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "\n             <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
                "\n             <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
                "\n             <statusCode code=\"completed\"/>",
                "\n             <value xsi:type=\"INT\" value=\"600\"/>",
                "\n             <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
                "\n         </observation>",
                "\n     </entryRelationship>",
                "\n </observation>",
                "\n</component>",
                "\n<component>",
                "\n <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "\n     <!-- ACI Numerator Denominator Type Measure Denominator Data templateId -->",
                "\n     <templateId root=\"2.16.840.1.113883.10.20.27.3.32\" extension=\"2016-09-01\"/>",
                "\n     <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>",
                "\n     <statusCode code=\"completed\"/>",
                "\n     <value xsi:type=\"CD\" code=\"DENOM\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\"/>",
                "\n     <!-- Denominator Count-->",
                "\n     <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">",
                "\n         <observation classCode=\"OBS\" moodCode=\"EVN\">",
                "\n             <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
                "\n             <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
                "\n             <statusCode code=\"completed\"/>",
                "\n             <value xsi:type=\"INT\" value=\"800\"/>",
                "\n             <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
                "\n         </observation>",
                "\n     </entryRelationship>",
                "\n </observation>",
                "\n</component>",
                "\n</organizer>",
                "\n</entry>",
                "\n</root>\n");
        return xmlFragment;
    }
}
