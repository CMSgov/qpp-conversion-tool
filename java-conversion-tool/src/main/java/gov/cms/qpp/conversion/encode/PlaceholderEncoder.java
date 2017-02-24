package gov.cms.qpp.conversion.encode;

import java.io.Writer;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "placeholder")
public class PlaceholderEncoder extends QppOutputEncoder {

	public PlaceholderEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// does not do anything except call write on any children

		for (Node child : node.getChildNodes()) {
			JsonOutputEncoder encoder = encoders.get(child.getId());

			encoder.encode(writer, child, indentLevel);
		}
	}
}
