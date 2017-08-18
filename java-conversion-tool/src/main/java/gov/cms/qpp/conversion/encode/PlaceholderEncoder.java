package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;

/**
 * Encoder to handle placeholder nodes.
 *
 * @author Scott Fradkin
 */
@Encoder(TemplateId.PLACEHOLDER)
public class PlaceholderEncoder extends QppOutputEncoder {

	public PlaceholderEncoder(Converter converter) {
		super(converter);
	}

	/**
	 * Encodes placeholder nodes into the wrapper
	 *
	 * @param wrapper object that will represent a placeholder
	 * @param node object that represents a placeholder
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {

		for (Node child : node.getChildNodes()) {
			JsonOutputEncoder encoder = encoders.get(child.getType());
			if (encoder != null) {
				encoder.encode(wrapper, child);
			} else {
				addValidationError(new Detail("Failed to find an encoder", child.getPath()));
			}
		}
	}
}
