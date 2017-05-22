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
	 * Writes the values of the node to the output encoder
	 * @param wrapper Json ouput
	 * @param node Internal representation of parsed xml elements
	 */
	private void encodeChild(JsonWrapper wrapper, Node node) {
		final String measurePerformedValue = "measurePerformed";

		Node child = node.findFirstNode(TemplateId.MEASURE_PERFORMED.getTemplateId());
		if (child != null) {
			String measureValue = child.getValue(measurePerformedValue);
			if ("Y".equalsIgnoreCase(measureValue) || "N".equalsIgnoreCase(measureValue)) {
				wrapper.putBoolean(QppOutputEncoder.VALUE, measureValue);
			} else {
				wrapper.putObject(QppOutputEncoder.VALUE, measureValue);
			}
		}
	}
}