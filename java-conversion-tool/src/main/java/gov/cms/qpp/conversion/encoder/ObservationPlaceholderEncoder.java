package gov.cms.qpp.conversion.encoder;

import java.io.Writer;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(elementName = "observation", templateId = "placeholder")
public class ObservationPlaceholderEncoder extends QppOutputEncoder {

	public ObservationPlaceholderEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// does not do anything except call write on any children

		for (Node child : node.getChildNodes()) {
			QppOutputEncoder encoder = (QppOutputEncoder) encoders.get(child.getIdElement(), child.getIdTemplate());

			encoder.encode(writer, child, indentLevel);
		}

	}

}
