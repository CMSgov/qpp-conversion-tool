package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class MeasurePerformedEncoderTest {

	@Test
	public void testMeasurePerformedEncodesIntoWrapper() throws EncodeException {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "Y");

		JsonWrapper jsonWrapper = new JsonWrapper();
		QppOutputEncoder qppOutputEncoder = new QppOutputEncoder();

		qppOutputEncoder.internalEncode(jsonWrapper, measurePerformedNode);

		assertThat("Must return correct encoded result", jsonWrapper.getBoolean("value"), is(true));
	}
}
