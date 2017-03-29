/*
 * Source code copyright Flexion
 * All rights reserved 
 */
package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import org.jdom2.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Provides unit test coverage for the AggregateCount Decoder
 * @author David Lauta
 */
public class AggregateCountDecoderTest {
    private static final String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
"<entry xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"+
"<component>\n" +
"<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
"        <templateId root=\"2.16.840.1.113883.10.20.27.3.5\" extension=\"2016-09-01\"/>\n" +
"        <templateId root=\"2.16.840.1.113883.10.20.27.3.16\" extension=\"2016-11-01\"/>\n" +
"        <code code=\"ASSERTION\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"Assertion\"/>\n" +
"        <statusCode code=\"completed\"/>\n" +
"        <value xsi:type=\"CD\" code=\"DENOM\" codeSystem=\"2.16.840.1.113883.5.1063\" codeSystemName=\"ObservationValue\"/>\n" +
"        <!--DENOM Count-->\n" +
"<entryRelationship typeCode=\"COMP\">\n" +
"        <observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
//"                <templateId root=\"2.16.840.1.113883.10.20.27.3.6\" extension=\"2016-09-01\"/>\n" +
//"                <templateId root=\"2.16.840.1.113883.10.20.27.3.21\" extension=\"2016-11-01\"/>\n" +
//"                <id root=\"95944FD2-241B-11E5-1027-09173F13E4C5\"/>\n" +
//"                <code code=\"76689-9\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Sex assigned at birth\"/>\n" +
//"                <statusCode code=\"completed\"/>\n" +
//"                <effectiveTime>\n" +
//"                        <low value=\"20170101\"/>\n" +
//"                        <high value=\"20171231\"/>\n" +
//"                </effectiveTime>\n" +
"                <value xsi:type=\"CD\" code=\"M\" codeSystem=\"2.16.840.1.113883.5.1\" codeSystemName=\"AdministrativeGenderCode\" displayName=\"Male\"/>\n" +
"                <entryRelationship typeCode=\"SUBJ\" inversionInd=\"true\">\n" +
"                        <observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
"                                <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>\n" +
//"                                <templateId root=\"2.16.840.1.113883.10.20.27.3.24\"/>\n" +
"                                <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>\n" +
"                                <statusCode code=\"completed\"/>\n" +
"                                <value xsi:type=\"INT\" value=\"400\"/>\n" +
"                                <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>\n" +
"                        </observation>\n" +
"                </entryRelationship>\n" +
"        </observation>\n" +
"</entryRelationship>\n"+
"</observation>\n"+
"</component>"+
"</entry>";
    public AggregateCountDecoderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of internalDecode method, of class AggregateCount.
     */
    @Test
    public void testInternalDecode() throws Exception{
        System.out.println("internalDecode");
        Element element = null;
        Node thisnode = null;
        AggregateCount instance = new AggregateCount();
        DecodeResult expResult = null;
        DecodeResult result = instance.internalDecode(element, thisnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSciNumeratorDenominatorOnNode method, of class AggregateCount.
     */
    @Test
    public void testSetSciNumeratorDenominatorOnNode() throws Exception {
        System.out.println("setSciNumeratorDenominatorOnNode");
        Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
        Element element = new Element("observation","","urn:hl7-org:v3");
        Node thisnode = root.findNode("2.16.840.1.113883.10.20.27.3.3").get(0);
        AggregateCount instance = new AggregateCount();
        instance.setSciNumeratorDenominatorOnNode(element, thisnode);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    /**
     * testAggregateCount parses xmlFragment and obtains the aggregate count value
     * @throws Exception 
     */
    @Test
    public void testAggregateCount() throws Exception {
        
        Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
        // remove default nodes (will fail if defaults change)
//        DefaultDecoder.removeDefaultNode(root.getChildNodes());

        // This node is the place holder around the root node
        assertThat("returned node should not be null", root, is(not(nullValue())));

        // For all decoders this should be either a value or child node
        assertThat("returned node should have one child node", root.getChildNodes().size(), is(1));
        // This is the child node that is produced by the intended decoder
        Node node = root.getChildNodes().get(0);
        // Should have a aggregate count node 
        assertThat("returned node should have one child decoder nodes", node.getChildNodes().size(), is(1));

        assertThat("DefaultDecoderFor should be Measure Data - CMS (V2)",
                (String) node.getValue("DefaultDecoderFor"), is("Measure Data - CMS (V2)"));

        List<String> testTemplateIds = new ArrayList<>();
        for (Node n : node.getChildNodes()) {
            testTemplateIds.add(n.getId());
        }

        assertThat("Should have Aggregate Count", testTemplateIds.contains("2.16.840.1.113883.10.20.27.3.3"), is(true));
    } 
}
