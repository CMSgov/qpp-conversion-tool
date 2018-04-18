package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

class MeasureDataEncoderTest {
	private enum Fixture {
		NUMER("performanceMet"),
		DENOM("eligiblePopulation"),
		DENEX("eligiblePopulationExclusion"),
		DENEXCEP("eligiblePopulationException");

		private String value;

		Fixture(String value) {
			this.value = value;
		}
	}

	@DisplayName("Verify measure data retrieval")
	@ParameterizedTest(name = "retrieval of ''{0}''")
	@EnumSource(Fixture.class)
	void testDenominator(Fixture fixture) throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode(fixture.name());
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertThat(jsonWrapper.getInteger(fixture.value))
				.isEqualTo(900);
	}

	private Node setUpMeasureDataNode(String measureType) {
		Node aggCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggCount.putValue(AGGREGATE_COUNT, "900");
		Node measureDataNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		measureDataNode.putValue(MEASURE_TYPE, measureType);
		measureDataNode.addChildNode(aggCount);
		return measureDataNode;
	}

	private JsonWrapper encode(Node measureDataNode) {
		JsonWrapper jsonWrapper = new JsonWrapper();
		QppOutputEncoder qppOutputEncoder = new QppOutputEncoder(new Context());
		qppOutputEncoder.internalEncode(jsonWrapper, measureDataNode);
		return jsonWrapper;
	}
}
