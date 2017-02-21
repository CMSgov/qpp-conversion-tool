package gov.cms.qpp.conversion.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;

public class QppXmlInputParserTest {

	@Test
	public void parseRateAggregationAsNode() {

		QppXmlInputParser parser = new QppXmlInputParser(true);

		Node rateAggrNode = parser.parse(new File("src/test/resources/qpp3-rateaggr-snippet.xml"));

		// the returned Node object from the snippet should be:
		// a top level placeholder node with a single child node that has the
		// "rateAggregationDenominator" key in it

		assertThat("returned node should not be null", rateAggrNode, is(not(nullValue())));

		assertThat("returned node should have one child node", rateAggrNode.getChildNodes().size(), is(1));

		assertThat("rate aggregation should be 600",
				(String) rateAggrNode.getChildNodes().get(0).get("rateAggregationDenominator"), is("600"));

	}

}
