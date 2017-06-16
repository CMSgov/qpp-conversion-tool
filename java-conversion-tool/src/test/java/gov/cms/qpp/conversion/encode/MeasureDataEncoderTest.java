package gov.cms.qpp.conversion.encode;

import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class MeasureDataEncoderTest {
	private final String PERFORMANCE_MET = "performanceMet"; //NUMER
	private final String ELIGIBLE_POPULATION = "eligiblePopulation";//DENUM
	private final String ELIGIBLE_POPULATION_EX = "eligiblePopulationExclusion";//DENEX
	private final String ELIGIBLE_POPULATION_EXCEP = "eligiblePopulationException";//DENEXCP

	@Test
	public void testDenominator() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("DENOM");
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertThat("Must return correct encoded result", jsonWrapper.getInteger(ELIGIBLE_POPULATION), is(900));
	}
	@Test
	public void testEligiblePopulationException() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("DENEXCEP");
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertThat("Must return correct encoded result", jsonWrapper.getInteger(ELIGIBLE_POPULATION_EXCEP), is(900));
	}
	@Test
	public void testEligiblePopulationExclusion() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("DENEX");
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertThat("Must return correct encoded result", jsonWrapper.getInteger(ELIGIBLE_POPULATION_EX), is(900));
	}

	@Test
	public void testPerformanceMet() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("NUMER");
		JsonWrapper jsonWrapper = encode(measureDataNode);
		assertThat("Must return correct encoded result", jsonWrapper.getInteger(PERFORMANCE_MET), is(900));
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
		QppOutputEncoder qppOutputEncoder = new QppOutputEncoder();
		qppOutputEncoder.internalEncode(jsonWrapper, measureDataNode);
		return jsonWrapper;
	}
}
