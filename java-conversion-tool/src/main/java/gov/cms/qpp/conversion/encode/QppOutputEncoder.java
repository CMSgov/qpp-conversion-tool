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

	/**
	 * Provide a means to associate json path to xpath expression when data is harvested from nodes by means
	 * other than extraction via the node's specified {@link Encoder}.
	 *
	 * @param wrapper parent {@link JsonWrapper}
	 * @param node decoded QRDA node
	 * @param leafLabel encoded json attribute name
	 */
	void maintainContinuity(JsonWrapper wrapper, Node node, String leafLabel) {
		JsonWrapper throwAway = new JsonWrapper();
		JsonOutputEncoder used = ENCODERS.get(node.getType());
		used.encode(throwAway, node);
		maintainContinuity(wrapper, throwAway, leafLabel);
	}

	/**
	 * Convenience override for {@link QppOutputEncoder#maintainContinuity(JsonWrapper, Node, String)}
	 *
	 * @param wrapper parent wrapper
	 * @param other wrapper whose metadata is to be merged with parent
	 * @param leafLabel json attribute name
	 */
	void maintainContinuity(JsonWrapper wrapper, JsonWrapper other, String leafLabel) {
		wrapper.mergeMetadata(other, leafLabel);
	}
}
