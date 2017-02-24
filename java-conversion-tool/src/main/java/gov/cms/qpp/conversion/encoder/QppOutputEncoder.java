package gov.cms.qpp.conversion.encoder;

import java.io.Writer;

import gov.cms.qpp.conversion.model.JsonEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;

public class QppOutputEncoder extends JsonOutputEncoder {

	protected static Registry<JsonOutputEncoder> encoders = new Registry<>(JsonEncoder.class);

	public QppOutputEncoder() {}

	
	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {

		// write nothing top level specific at this point
		// check the encoder Registry for
		// an Encoder to call for the Node, and then call its children

		JsonOutputEncoder encoder = encoders.get(node.getId());

		if (null == encoder) {
			return;
		}
		encoder.encode(writer, node, indentLevel);

		for (Node child : node.getChildNodes()) {
			encoder = encoders.get(child.getId());

			if (null != encoder) {
				encoder.encode(writer, child, indentLevel + 1);
			}
		}
	}

}
