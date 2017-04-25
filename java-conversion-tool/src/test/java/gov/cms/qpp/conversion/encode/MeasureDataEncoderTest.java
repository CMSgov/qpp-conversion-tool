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
	public void testMeasureDataEncoding() throws EncodeException {
		Node aggCount = new Node(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		aggCount.putValue(AGGREGATE_COUNT, "900");
		Node measureDataNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		measureDataNode.putValue(MEASURE_TYPE, "some type");
		measureDataNode.addChildNode(aggCount);

		JsonWrapper jsonWrapper = new JsonWrapper();
		QppOutputEncoder qppOutputEncoder = new QppOutputEncoder();

		qppOutputEncoder.internalEncode(jsonWrapper, measureDataNode);

		assertThat("Must return correct encoded result", jsonWrapper.getInteger("some type"), is(900));
	}
}
