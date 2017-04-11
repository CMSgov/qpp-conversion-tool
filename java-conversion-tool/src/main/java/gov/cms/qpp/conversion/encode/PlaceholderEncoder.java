package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to handle placeholder nodes.
 *
 * @author Scott Fradkin
 *
 */
@Encoder(TemplateId.PLACEHOLDER)
public class PlaceholderEncoder extends QppOutputEncoder {

	/**
	 * Encodes placeholder nodes into the wrapper
	 *
	 * @param wrapper object that will represent a placeholder
	 * @param node object that represents a placeholder
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {

		for (Node child : node.getChildNodes()) {
			String templateId = child.getId();
			JsonOutputEncoder encoder = ENCODERS.get(templateId);
			if (encoder == null) {
				addValidation(templateId, "Failed to find an encoder");
			} else {
				encoder.encode(wrapper, child);
			}
		}
	}
}
