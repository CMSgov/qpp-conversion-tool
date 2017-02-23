package gov.cms.qpp.conversion.encoder;

import java.io.Writer;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;

public class QppOutputEncoder extends JsonOutputEncoder {

	protected static Registry<OutputEncoder> encoders = new Registry<>(Encoder.class);

	public QppOutputEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {

		// write nothing top level specific at this point
		// check the encoder Registry for
		// an Encoder to call for the Node, and then call its children

		JsonOutputEncoder encoder = (JsonOutputEncoder) encoders.get(node.getIdElement(), node.getIdTemplate());

		if (null != encoder) {
			encoder.encode(writer, node, indentLevel);

			for (Node child : node.getChildNodes()) {
				encoder = (JsonOutputEncoder) encoders.get(child.getIdElement(), child.getIdTemplate());

				if (null != encoder) {
					encoder.encode(writer, child, indentLevel + 1);
				}
			}
		}
	}

}
