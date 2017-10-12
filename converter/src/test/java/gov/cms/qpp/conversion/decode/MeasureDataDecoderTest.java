package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

public class MeasureDataDecoderTest {

	private static String happy;

	private Context context;
	private Node placeholder;

	@BeforeClass
	public static void setup() throws IOException {
		happy = TestHelper.getFixture("measureDataHappy.xml");
	}

	@Before
	public void before() throws XmlException {
		context = new Context();
		MeasureDataDecoder measureDataDecoder = new MeasureDataDecoder(context);
		placeholder = measureDataDecoder.decode(XmlUtils.stringToDom(happy));
	}

	@Test
	public void testDecodeOfDenomMeasureData() {
		sharedTest(SubPopulations.DENOM);
	}

	@Test
	public void testDecodeOfNumerMeasureData() {
		sharedTest(SubPopulations.NUMER);
	}

	@Test
	public void testDecodeOfDenexMeasureData() {
		sharedTest(SubPopulations.DENEX);
	}

	@Test
	public void testDecodeOfDenexcepMeasureData() {
		sharedTest(SubPopulations.DENEXCEP);
	}

	private void sharedTest(String type) {
		Node measure =  placeholder.findChildNode(node -> node.getValue(MEASURE_TYPE).equals(type));

		String message = String.format("Should have a %s value", type);
		assertWithMessage(message)
				.that(measure)
				.isNotNull();
		assertWithMessage("Should have an aggregate count child")
				.that(measure.getChildNodes().get(0).getType())
				.isEquivalentAccordingToCompareTo(TemplateId.ACI_AGGREGATE_COUNT);
	}
}
