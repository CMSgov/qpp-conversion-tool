package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class MeasurePerformedEncoderTest {

	@Test
	void testMeasurePerformedEncodesIntoWrapper() throws EncodeException {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "Y");

		JsonWrapper jsonWrapper = new JsonWrapper();
		QppOutputEncoder qppOutputEncoder = new QppOutputEncoder(new Context());

		qppOutputEncoder.internalEncode(jsonWrapper, measurePerformedNode);

		assertThat(jsonWrapper.getBoolean("value")).isTrue();
	}
}
