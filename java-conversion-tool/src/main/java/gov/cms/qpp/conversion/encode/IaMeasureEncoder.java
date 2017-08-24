package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;

/**
 * Encoder to serialize Improvement Activity Performed Measure Reference and Results
 */
@Encoder(TemplateId.IA_MEASURE)
public class IaMeasureEncoder extends QppOutputEncoder {

	public IaMeasureEncoder(Context context) {
		super(context);
	}

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
			JsonOutputEncoder measurePerformedEncoder = encoders.get(measurePerformedNode.getType());

			JsonWrapper value = new JsonWrapper();
			measurePerformedEncoder.encode(value, measurePerformedNode);
			maintainContinuity(wrapper, value, VALUE);

			if (null != value.getBoolean(VALUE)) {
				wrapper.putObject(VALUE, value.getBoolean(VALUE));
			}
		}
	}
}
