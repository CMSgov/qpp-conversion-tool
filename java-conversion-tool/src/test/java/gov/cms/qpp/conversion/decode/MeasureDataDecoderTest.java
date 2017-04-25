package gov.cms.qpp.conversion.decode;


import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class MeasureDataDecoderTest extends BaseTest {
	private static String happy;

	@BeforeClass
	public static void setup() throws IOException {
		happy = getFixture("measureDataHappy.xml");
	}

	@Test
	public void testDecodeOfMeasureData() throws XmlException {
		MeasureDataDecoder measureDataDecoder = new MeasureDataDecoder();
		Node placeholder = measureDataDecoder.decode(XmlUtils.stringToDom(happy));
		Node measure =  placeholder.getChildNodes().get(0);

		assertThat("Should have a 'DENOM' measure",
				measure.getValue(MEASURE_TYPE), is("DENOM"));
		assertThat("Should have an aggregate count child",
				measure.getChildNodes().get(0).getType(), is(TemplateId.ACI_AGGREGATE_COUNT));
	}

	@Test
	public void testIgnoreOfUnmappedMeasureData() throws XmlException {
		MeasureDataDecoder measureDataDecoder = new MeasureDataDecoder();
		Node placeholder = measureDataDecoder.decode(XmlUtils.stringToDom(happy));

		assertThat("Should have one child", placeholder.getChildNodes(), hasSize(1));
	}

}
