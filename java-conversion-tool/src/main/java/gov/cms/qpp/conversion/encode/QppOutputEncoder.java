package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;

/**
 * Top level Encoder for serializing into QPP format.
 */
public class QppOutputEncoder extends JsonOutputEncoder {

	public static final String VALUE = "value";
	protected static final Registry<JsonOutputEncoder> ENCODERS = new Registry<>(Encoder.class);

	/**
	 * Top level internalEncode that calls it's children from the registry.
	 * Each encoder calls its child encoder with an encode() method
	 *
	 * @param wrapper object to encode into
	 * @param node object to encode
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		JsonOutputEncoder encoder = ENCODERS.get(node.getType());

		if (null != encoder) {
			encoder.encode(wrapper, node);
		}
	}

	protected void maintainContinuity(JsonWrapper wrapper, Node node, String leafLabel) {
		if (node == null) {
			return;
		}
		JsonWrapper throwAway = new JsonWrapper();
		JsonOutputEncoder used = ENCODERS.get(node.getType());
		used.encode(throwAway, node);
		maintainContinuity(wrapper, throwAway, leafLabel);
	}

	protected void maintainContinuity(JsonWrapper wrapper, JsonWrapper other, String leafLabel) {
		wrapper.mergeMetadata(other, leafLabel);
	}
}
