package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "placeholder")
public class PlaceholderEncoder extends QppOutputEncoder {

	public PlaceholderEncoder() {
	}

	@Override
	public void encode(JsonWrapper wrapper, Node node) {
		// does not do anything except call write on any children

		for (Node child : node.getChildNodes()) {
			JsonOutputEncoder encoder = encoders.get(child.getId());

			encoder.encode(wrapper, child);
		}
	}
}
