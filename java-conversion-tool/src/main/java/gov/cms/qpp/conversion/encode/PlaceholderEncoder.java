package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Encoder to handle placeholder nodes. 
 * @author Scott Fradkin
 * 
 */
@Encoder(templateId = "placeholder")
public class PlaceholderEncoder extends QppOutputEncoder {

	public PlaceholderEncoder() {
	}

	@Override
	protected void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {
		// does not do anything except call write on any children

		for (Node child : node.getChildNodes()) {
			String templateId = child.getId();
			JsonOutputEncoder encoder = encoders.get(templateId);
			if (encoder == null) {
				addValidation(templateId, "Failed to find an encoder");
			} else {
				encoder.encode(wrapper, child);
			}
		}
	}
}
