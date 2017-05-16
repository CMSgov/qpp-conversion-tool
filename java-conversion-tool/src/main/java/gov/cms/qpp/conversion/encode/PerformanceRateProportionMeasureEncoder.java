package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encodes a Performance Rate Proportion Measure
 */
@Encoder(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2)
public class PerformanceRateProportionMeasureEncoder extends QppOutputEncoder {

	/**
	 * Encodes a Performance Rate Proportion Measure from the current node into json
	 *
	 * @param wrapper object to encode into
	 * @param node object to encode
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.putString("performanceRateId", node.getValue("performanceRateId"));
		wrapper.putFloat("value" , node.getValue("performanceRateValue"));
	}
}
