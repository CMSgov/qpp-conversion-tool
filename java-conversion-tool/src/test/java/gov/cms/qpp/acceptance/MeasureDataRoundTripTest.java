package gov.cms.qpp.acceptance;


import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;


public class MeasureDataRoundTripTest extends BaseTest {
	private static String happy;
	private static String expected = "{\n  \"IPOP\" : 950,\n  \"DENOM\" : 950,\n  \"NUMER\" : 900\n}";

	@BeforeClass
	public static void setup() throws IOException {
		happy = getFixture("measureDataHappy.xml");
	}


	@Test
	public void decodeMeasureDataAsNode() throws Exception {
		for (String s : Arrays.asList("DENOM", "IPOP", "NUMER", "DENEX", "DENEXCEP")) {
			test(s);
		}
	}

	private void test(String type) throws Exception {
		//setup
		Node placeholder = new QppXmlDecoder().decode(XmlUtils.stringToDom(happy));
		Node measure =  placeholder.findChildNode(n -> n.getValue(MEASURE_TYPE).equals(type));
		String message = String.format("Should have a %s measure", type);

		//when
		StringWriter sw = encode(placeholder);

		//then
		assertNotNull(message, measure);
		assertThat("Should have an aggregate count child",
				measure.getChildNodes().get(0).getType(), is(TemplateId.ACI_AGGREGATE_COUNT));
		assertThat("expected encoder to return a single measure data", sw.toString(), is(expected));
	}

	private StringWriter encode(Node placeholder) throws EncodeException {
		QppOutputEncoder encoder = new QppOutputEncoder();
		List<Node> nodes = new ArrayList<>();
		nodes.add(placeholder);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));
		return sw;
	}
}
