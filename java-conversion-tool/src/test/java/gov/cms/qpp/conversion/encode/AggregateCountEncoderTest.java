package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class AggregateCountEncoderTest {

    private Node numeratorDenominatorNode;
    private List<Node> nodes;

    public AggregateCountEncoderTest() {
    }

    /**
     * Set up a default node to be pass to an encoder
     */
    @Before
    public void createNode() {
        numeratorDenominatorNode = new Node();
        numeratorDenominatorNode.setId("2.16.840.1.113883.10.20.27.3.3");
        numeratorDenominatorNode.putValue("aggregateCount", "600");

        nodes = new ArrayList<>();
        nodes.add(numeratorDenominatorNode);
    }

    /**
     * Test Function for the QppOutputEncoder
     */
    @Test
    public void testEncoderWithFramework() {
        QppOutputEncoder encoder = new QppOutputEncoder();

        encoder.setNodes(nodes);

        StringWriter sw = new StringWriter();

        try {
            encoder.encode(new BufferedWriter(sw));
        } catch (EncodeException e) {
            fail("Failure to encode: " + e.getMessage());
        }

        // NOTE: This test is only relevant in that it finds the deep value but it is not actually a result
        String EXPECTED = "{\n  \"value\" : 600\n}";
        assertThat("expected encoder to return a single number numerator/denominator", sw.toString(), is(EXPECTED));
    }

    /**
     * Test Function for the AggregateCountEncode
     */
    @Test
    public void testEncoder() {
        AggregateCountEncoder encoder = new AggregateCountEncoder();
        encoder.setNodes(nodes);
        JsonWrapper json = new JsonWrapper();
        try {
            encoder.internalEncode(json, numeratorDenominatorNode);
        } catch (EncodeException e) {
            fail("Failure to encode: " + e.getMessage());
        }
        assertThat("expected encoder to return a single number numerator/denominator", json.getInteger("value"), is(600));
    }

}
