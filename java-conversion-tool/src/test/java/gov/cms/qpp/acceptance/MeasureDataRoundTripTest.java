package gov.cms.qpp.acceptance;


import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class MeasureDataRoundTripTest extends BaseTest {
	private static String happy;

	@BeforeClass
	public static void setup() throws IOException {
		happy = getFixture("measureDataHappy.xml");
	}

	@Test
	public void decodeMeasureDataAsNode() throws Exception {
		Node placeholder = new QppXmlDecoder().decode(XmlUtils.stringToDom(happy));
		Node measure =  placeholder.getChildNodes().get(0);

		assertThat("Should have a 'DENOM' measure",
				measure.getValue(MEASURE_TYPE), is("DENOM"));
		assertThat("Should have an aggregate count child",
				measure.getChildNodes().get(0).getType(), is(TemplateId.ACI_AGGREGATE_COUNT));

		QppOutputEncoder encoder = new QppOutputEncoder();
		List<Node> nodes = new ArrayList<>();
		nodes.add(placeholder);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));

		String expected = "{\n  \"DENOM\" : 950\n}";
		assertThat("expected encoder to return a single measure data", sw.toString(), is(expected));
	}
}
