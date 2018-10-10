package gov.cms.qpp.conversion.decode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

class PerformanceRateProportionMeasureDecoderTest {
	private static String happy;
	private static String nullHappy;

	private Context context;
	private Node placeholder;
	private Node performanceRateNode;

	@BeforeAll
	static void setup() throws IOException {
		happy = TestHelper.getFixture("measureDataWithPerformanceRate.xml");
		nullHappy = TestHelper.getFixture("measureDataWithNullPerformanceRate.xml");
	}

	@BeforeEach
	void before() throws XmlException {
		decodeNodeFromFile(happy);
		performanceRateNode = getNode();
	}

	@Test
	void testPerformanceRateValueSuccess() {
		assertThat(performanceRateNode.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE))
				.isEqualTo("0.947368");
	}

	@Test
	void testPerformanceRateUuidSuccess() {
		final String performanceRateId = "6D01A564-58CC-4CF5-929F-B83583701BFE";
		assertThat(performanceRateNode.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID))
				.isEqualTo(performanceRateId);
	}

	@Test
	void testSuccessfulNullPerformanceRate() throws XmlException {
		decodeNodeFromFile(nullHappy);
		performanceRateNode = getNode();
		assertThat(performanceRateNode.getValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE))
				.isEqualTo("NA");
	}

	private void decodeNodeFromFile(String filename) throws XmlException {
		context = new Context();
		PerformanceRateProportionMeasureDecoder decoder = new PerformanceRateProportionMeasureDecoder(context);
		QrdaDecoderEngine engine = new QrdaDecoderEngine(context);
		placeholder = engine.decode(XmlUtils.stringToDom(filename));
	}

	private Node getNode() {
		return placeholder.findFirstNode(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
	}
}
