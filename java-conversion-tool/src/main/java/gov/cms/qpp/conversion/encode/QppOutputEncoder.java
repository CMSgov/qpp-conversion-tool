package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.EncoderNew;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;

/**
 * Top level Encoder for serializing into QPP format.
 *
 * @author Scott Fradkin
 *
 */
public class QppOutputEncoder extends JsonOutputEncoder {

	protected static final Registry<String, JsonOutputEncoder> ENCODERS = new Registry<>(Encoder.class, EncoderNew.class);

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {

		// write nothing top level specific at this point
		// check the encoder Registry for
		// an Encoder to call for the Node, and then call its children
		JsonOutputEncoder encoder = ENCODERS.get(node.getId());

		if (null != encoder) {
			encoder.encode(wrapper, node);

			// each Node understands whether or not it has children and the
			// corresponding Encoders
			// also understand this, so we leave it to each Encoder
			// implementation to call encode() on
			// the child nodes of the node it's encoding
		}
	}

}
