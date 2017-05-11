package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;

/**
 * Top level Encoder for serializing into QPP format.
 */
public class QppOutputEncoder extends JsonOutputEncoder {

	protected static Registry<String, JsonOutputEncoder> encoders = new Registry<>(Encoder.class);

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
		JsonOutputEncoder encoder = encoders.get(node.getId());

		if (null != encoder) {
			encoder.encode(wrapper, node);
		}
	}

}
