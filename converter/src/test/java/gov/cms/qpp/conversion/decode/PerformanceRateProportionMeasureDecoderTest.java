package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.model.Template;
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
	private Node performanceRateNode;

	@BeforeClass
	public static void setup() throws IOException {
		happy = TestHelper.getFixture("measureDataWithPerformanceRate.xml");
		nullHappy = TestHelper.getFixture("measureDataWithNullPerformanceRate.xml");
	}

	@Before
	public void before() throws XmlException {
		decodeNodeFromFile(happy);
		performanceRateNode = getNode();
	}

	@Test
	public void testPerformanceRateValueSuccess() {
		assertThat("Must contain the correct value",
				performanceRateNode.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE), is("0.947368"));
	}

	@Test
	public void testPerformanceRateUuidSuccess() {
		final String performanceRateId = "6D01A564-58CC-4CF5-929F-B83583701BFE";
		assertThat("Must contain the correct UUID",
				performanceRateNode.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID), is(performanceRateId));
	}

	@Test
	public void testSuccessfulNullPerformanceRate() throws XmlException {
		decodeNodeFromFile(nullHappy);
		performanceRateNode = getNode();
		assertThat("Must contain the correct value",
				performanceRateNode.getValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE), is("NA"));
	}

	private void decodeNodeFromFile(String filename) throws XmlException {
		context = new Context();
		PerformanceRateProportionMeasureDecoder decoder = new PerformanceRateProportionMeasureDecoder(context);
		placeholder = decoder.decode(XmlUtils.stringToDom(filename));
	}

	private Node getNode() {
		return placeholder.findFirstNode(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
	}
}
