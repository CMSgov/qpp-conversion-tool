package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

class MeasureDataDecoderTest {

	private static String happy;

	private Node placeholder;

	@BeforeAll
	static void setup() throws IOException {
		happy = TestHelper.getFixture("measureDataHappy.xml");
	}

	@BeforeEach
	void before() throws XmlException {
		Context context = new Context();
		QrdaDecoderEngine engine = new QrdaDecoderEngine(context);
		placeholder = engine.decode(XmlUtils.stringToDom(happy));
	}

	@ParameterizedTest
	@EnumSource(value = SubPopulationLabel.class, mode = EnumSource.Mode.EXCLUDE, names = {"IPOP"})
	void testDecodeOfMeasureData(SubPopulationLabel label) {
		sharedTest(label.name());
	}

	private void sharedTest(String type) {
		Node measure =  placeholder.findChildNode(node -> node.getValue(MEASURE_TYPE).equals(type));

		assertThat(measure).isNotNull();
		assertThat(measure.getChildNodes().get(0).getType()).isEqualTo(TemplateId.ACI_AGGREGATE_COUNT);
	}
}
