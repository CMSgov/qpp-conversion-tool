package gov.cms.qpp.conversion.decode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

class MeasureDataDecoderTest {

	private static String happy;

	private Context context;
	private Node placeholder;

	@BeforeAll
	static void setup() throws IOException {
		happy = TestHelper.getFixture("measureDataHappy.xml");
	}

	@BeforeEach
	void before() throws XmlException {
		context = new Context();
		MeasureDataDecoder measureDataDecoder = new MeasureDataDecoder(context);
		QrdaDecoderEngine engine = new QrdaDecoderEngine(context);
		placeholder = engine.decode(XmlUtils.stringToDom(happy));
	}

	@Test
	void testDecodeOfDenomMeasureData() {
		sharedTest(SubPopulations.DENOM);
	}

	@Test
	void testDecodeOfNumerMeasureData() {
		sharedTest(SubPopulations.NUMER);
	}

	@Test
	void testDecodeOfDenexMeasureData() {
		sharedTest(SubPopulations.DENEX);
	}

	@Test
	void testDecodeOfDenexcepMeasureData() {
		sharedTest(SubPopulations.DENEXCEP);
	}

	private void sharedTest(String type) {
		Node measure =  placeholder.findChildNode(node -> node.getValue(MEASURE_TYPE).equals(type));

		assertThat(measure).isNotNull();
		assertThat(measure.getChildNodes().get(0).getType()).isEqualTo(TemplateId.ACI_AGGREGATE_COUNT);
	}
}
