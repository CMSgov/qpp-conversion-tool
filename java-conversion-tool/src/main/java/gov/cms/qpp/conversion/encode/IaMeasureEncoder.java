package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;

/**
 * Encoder to serialize Improvement Activity Performed Measure Reference and Results
 */
@Encoder(TemplateId.IA_MEASURE)
public class IaMeasureEncoder extends QppOutputEncoder {
	private static String VALUE_STRING = "value";

	/**
	 * internalEncode to encode the IA Performed Measure
	 *
	 * @param wrapper object that will represent a IA Performed Measure
	 * @param node object that represents a IA Performed Measure
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.putObject("measureId", node.getValue("measureId"));

		List<Node> children = node.getChildNodes();

		if (!children.isEmpty()) {
			Node measurePerformedNode = children.get(0);
			JsonOutputEncoder measurePerformedEncoder = ENCODERS.get(measurePerformedNode.getId());

			JsonWrapper value = new JsonWrapper();
			measurePerformedEncoder.encode(value, measurePerformedNode);

			if (null != value.getBoolean(VALUE_STRING)) {
				wrapper.putObject(VALUE_STRING, value.getBoolean(VALUE_STRING));
			}
		}
	}
}
