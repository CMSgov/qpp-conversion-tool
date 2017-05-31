package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MeasureDataEncoderTest {

	@Test
	public void testIpop() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("IPOP");

		JsonWrapper jsonWrapper = encode(measureDataNode);

		assertThat("Must return correct encoded result", jsonWrapper.getInteger("initialPopulation"), is(900));
	}

	@Test
	public void testIpp() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("IPP");

		JsonWrapper jsonWrapper = encode(measureDataNode);

		assertThat("Must return correct encoded result", jsonWrapper.getInteger("initialPopulation"), is(900));
	}

	@Test
	public void testDenominator() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("DENOM");

		JsonWrapper jsonWrapper = encode(measureDataNode);

		assertThat("Must return correct encoded result", jsonWrapper.getInteger("denominator"), is(900));
	}
	@Test
	public void testDenominatorException() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("DENEXCEP");

		JsonWrapper jsonWrapper = encode(measureDataNode);

		assertThat("Must return correct encoded result", jsonWrapper.getInteger("denominatorExceptions"), is(900));
	}
	@Test
	public void testDenominatorExclusion() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("DENEX");

		JsonWrapper jsonWrapper = encode(measureDataNode);

		assertThat("Must return correct encoded result", jsonWrapper.getInteger("denominatorExclusions"), is(900));
	}

	@Test
	public void testNumerator() throws EncodeException {
		Node measureDataNode = setUpMeasureDataNode("NUMER");

		JsonWrapper jsonWrapper = encode(measureDataNode);

		assertThat("Must return correct encoded result", jsonWrapper.getInteger("numerator"), is(900));
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
