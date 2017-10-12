package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class MeasurePerformedEncoderTest {

	@Test
	public void testMeasurePerformedEncodesIntoWrapper() throws EncodeException {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "Y");

		JsonWrapper jsonWrapper = new JsonWrapper();
		QppOutputEncoder qppOutputEncoder = new QppOutputEncoder(new Context());

		qppOutputEncoder.internalEncode(jsonWrapper, measurePerformedNode);

		assertWithMessage("Must return correct encoded result")
				.that(jsonWrapper.getBoolean("value")).isTrue();
	}
}
