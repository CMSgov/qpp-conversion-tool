package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.ConverterTestHelper;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;

public class MeasureDataDecoderTest {

	private static String happy;

	private Converter converter;
	private Node placeholder;

	@BeforeClass
	public static void setup() throws IOException {
		happy = getFixture("measureDataHappy.xml");
	}

	@Before
	public void before() throws XmlException {
		converter = ConverterTestHelper.newMockConverter();
		MeasureDataDecoder measureDataDecoder = new MeasureDataDecoder(converter);
		placeholder = measureDataDecoder.decode(XmlUtils.stringToDom(happy));
	}

	@Test
	public void testDecodeOfDenomMeasureData() {
		sharedTest("DENOM");
	}

	@Test
	public void testDecodeOfNumerMeasureData() {
		sharedTest("NUMER");
	}

	@Test
	public void testDecodeOfDenexMeasureData() {
		sharedTest("DENEX");
	}

	@Test
	public void testDecodeOfDenexcepMeasureData() {
		sharedTest("DENEXCEP");
	}

	private void sharedTest(String type) {
		Node measure =  placeholder.findChildNode( node -> node.getValue(MEASURE_TYPE).equals(type));

		String message = String.format("Should have a %s value", type);
		assertNotNull(message, measure);
		assertThat("Should have an aggregate count child",
				measure.getChildNodes().get(0).getType(), is(TemplateId.ACI_AGGREGATE_COUNT));
	}

	@Test
	public void testIgnoreOfUnmappedMeasureData() throws XmlException {
		MeasureDataDecoder measureDataDecoder = new MeasureDataDecoder(converter);
		Node placeholder = measureDataDecoder.decode(XmlUtils.stringToDom(happy));

		assertThat("Should have four children", placeholder.getChildNodes(), hasSize(4));
	}

}
