package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;

public class QppOutputEncoder extends JsonOutputEncoder {

	protected static Registry<String, JsonOutputEncoder> encoders = new Registry<>(Encoder.class);

	public QppOutputEncoder() {
	}

	@Override
	public void encode(JsonWrapper wrapper, Node node) {

		// write nothing top level specific at this point
		// check the encoder Registry for
		// an Encoder to call for the Node, and then call its children

		JsonOutputEncoder encoder = encoders.get(node.getId());

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
