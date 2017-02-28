package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "placeholder")
public class PlaceholderEncoder extends QppOutputEncoder {

	public PlaceholderEncoder() {
	}

	@Override
	protected void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {
		// does not do anything except call write on any children

		for (Node child : node.getChildNodes()) {
			JsonOutputEncoder encoder = encoders.get(child.getId());

			encoder.encode(wrapper, child);
		}
	}
}
