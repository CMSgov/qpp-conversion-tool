package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to serialize numerator data from a Numerator/Denominator Type Measure.
 */
@Encoder(TemplateId.PI_NUMERATOR)
public class PiProportionNumeratorEncoder extends QppOutputEncoder {

	private static final String ENCODE_LABEL = "numerator";

	public PiProportionNumeratorEncoder(Context context) {
		super(context);
	}

	/**
	 *  Encodes an PI Numerator Measure into the QPP format
	 *
	 * @param wrapper Wrapper that will represent the PI Numerator Measure
	 * @param node Node that represents the PI Numerator Measure
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {

		Node piNumeratorNode = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);

		if (piNumeratorNode != null) {
			JsonWrapper numerator = encodeChild(piNumeratorNode);

			if (null != numerator.getInteger(VALUE)) {
				wrapper.put(ENCODE_LABEL, numerator.getInteger(VALUE));
				wrapper.mergeMetadata(numerator, ENCODE_LABEL);
			}
		}
	}

	private JsonWrapper encodeChild(Node numeratorValueNode) {
		JsonOutputEncoder numeratorValueEncoder = encoders.get(numeratorValueNode.getType());

		JsonWrapper jsonWrapper = new JsonWrapper();
		numeratorValueEncoder.encode(jsonWrapper, numeratorValueNode);

		return jsonWrapper;
	}
}
