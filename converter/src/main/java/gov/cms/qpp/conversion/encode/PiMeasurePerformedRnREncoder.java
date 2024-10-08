package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import static gov.cms.qpp.conversion.model.Constants.VALUE;

@Encoder(TemplateId.PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
public class PiMeasurePerformedRnREncoder extends QppOutputEncoder {

	public PiMeasurePerformedRnREncoder(Context context) {
		super(context);
	}

	/**
	 *  Encodes an PI measure performed reference and results node into the QPP format
	 *
	 * @param wrapper Wrapper representing the {@link TemplateId#PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS} type
	 * @param node Node that represents the {@link TemplateId#PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS}
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.put("measureId", node.getValue("measureId"));
		encodeChild(wrapper, node);
	}

	/**
	 * Writes the values of the node to the output encoder
	 * @param wrapper Json ouput
	 * @param node Internal representation of parsed xml elements
	 */
	private void encodeChild(JsonWrapper wrapper, Node node) {
		Node child = node.findFirstNode(TemplateId.MEASURE_PERFORMED);
		if (child != null) {
			String measureValue = child.getValue("measurePerformed");
			if ("Y".equalsIgnoreCase(measureValue) || "N".equalsIgnoreCase(measureValue)) {
				wrapper.putBoolean(VALUE, measureValue);
			} else {
				wrapper.put(VALUE, measureValue);
			}
		}
	}
}