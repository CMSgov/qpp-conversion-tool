package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

class MeasureDataEncoderTest {
	private final String PERFORMANCE_MET = "performanceMet"; //NUMER
	private final String ELIGIBLE_POPULATION = "eligiblePopulation";//DENUM
	private final String ELIGIBLE_POPULATION_EX = "eligiblePopulationExclusion";//DENEX
	private final String ELIGIBLE_POPULATION_EXCEP = "eligiblePopulationException";//DENEXCP

	@Test
	void testDenominator() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode(SubPopulations.DENOM);
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertWithMessage("Must return correct encoded result")
				.that(jsonWrapper.getInteger(ELIGIBLE_POPULATION))
				.isEqualTo(900);
	}

	@Test
	void testEligiblePopulationException() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode(SubPopulations.DENEXCEP);
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertWithMessage("Must return correct encoded result")
				.that(jsonWrapper.getInteger(ELIGIBLE_POPULATION_EXCEP))
				.isEqualTo(900);
	}

	@Test
	void testEligiblePopulationExclusion() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode(SubPopulations.DENEX);
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertWithMessage("Must return correct encoded result")
				.that(jsonWrapper.getInteger(ELIGIBLE_POPULATION_EX))
				.isEqualTo(900);
	}

	@Test
	void testPerformanceMet() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode(SubPopulations.NUMER);
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertWithMessage("Must return correct encoded result")
				.that(jsonWrapper.getInteger(PERFORMANCE_MET))
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
