package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * This will test the QualityMeasureId encoder
 */
public class QualityMeasureIdEncoderTest {

	/**
	 * Tests the internalEncode when the Node is valid
	 *
	 * @throws Exception
	 */
	@Test
	public void internalEncodeValidTest() throws Exception {
		Node root = new Node();
		Node qualityMeasureId = new Node(root, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId());
		qualityMeasureId.putValue("measureId", "Measure Id Value");

		Node denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denominatorNode.putValue("type", "DENOM");

		Node aggCount = new Node();
		aggCount.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		aggCount.putValue("aggregateCount", "600");
		denominatorNode.addChildNode(aggCount);

		qualityMeasureId.addChildNode(denominatorNode);

		QualityMeasureIdEncoder encoder = new QualityMeasureIdEncoder();
		JsonWrapper json = new JsonWrapper();
		try {
			encoder.internalEncode(json, qualityMeasureId);
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}
		assertThat("expected encoder to return a single value", json.getString("measureId"), is("Measure Id Value"));

	}

}