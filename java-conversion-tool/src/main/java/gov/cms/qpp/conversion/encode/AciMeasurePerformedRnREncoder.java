package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

@Encoder(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
public class AciMeasurePerformedRnREncoder extends QppOutputEncoder {

	/**
	 *  Encodes an ACI measure performed reference and results node into the QPP format
	 *
	 * @param wrapper Wrapper representing the {@link TemplateId#ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS} type
	 * @param node Node that represents the {@link TemplateId#ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS}
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.putObject("measureId", node.getValue("measureId"));
		encodeChild(wrapper, node);
	}

	/**
	 *
	 * @param wrapper
	 * @param node
	 */
	private void encodeChild(JsonWrapper wrapper, Node node) {
		final String measurePerformedValue = "measurePerformed";
		final String value = "value";
		Node child = node.findFirstNode(TemplateId.MEASURE_PERFORMED.getTemplateId());
		if (child != null) {
			wrapper.putObject(value, child.getValue(measurePerformedValue));
			if (null != wrapper.getBoolean(measurePerformedValue)) {
				wrapper.putObject(value, wrapper.getBoolean(measurePerformedValue));
			}
		}
	}
}