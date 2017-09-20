package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PerformanceRateProportionMeasureDecoderTest {
	private static String happy;
	private static String nullHappy;

	private Context context;
	private Node placeholder;

	@BeforeClass
	public static void setup() throws IOException {
		happy = TestHelper.getFixture("measureDataWithPerformanceRate.xml");
		nullHappy = TestHelper.getFixture("measureDataWithNullPerformanceRate.xml");
	}

	@Before
	public void before() throws XmlException {
		decodeNodeFromFile(happy);
	}

	@Test
	public void testSuccessfulPerformanceRate() {
		Stream<Node> performanceRateNodes = getNodeStream();
		performanceRateNodes.forEach(node ->
				assertThat("Must contain correct value",
						node.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE), is("0.947368"))
		);
	}

	@Test
	public void testSuccessfulNullPerformanceRate() throws XmlException {
		decodeNodeFromFile(nullHappy);
		Stream<Node> performanceRateNodes = getNodeStream();
		performanceRateNodes.forEach(node ->
				assertThat("Must contain correct value",
						node.getValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE), is("NA"))
		);
	}

	private void decodeNodeFromFile(String filename) throws XmlException {
		context = new Context();
		PerformanceRateProportionMeasureDecoder decoder = new PerformanceRateProportionMeasureDecoder(context);
		placeholder = decoder.decode(XmlUtils.stringToDom(filename));
	}

	private Stream<Node> getNodeStream() {
		return placeholder.getChildNodes(
				node -> node.getType().equals(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE));
	}
}
