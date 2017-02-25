package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Encodes the "resultName" of the Q.E.D. node as a placeholder for testing
 * 
 */
@Encoder(templateId = "Q.E.D")
public class QedEncoder extends QppOutputEncoder {

	public QedEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {

		// the qed node should have one key/value in it
		Set<String> keys = node.getKeys();

		int index = 1;

		try {
			writeIndents(writer, indentLevel);

			for (String key : keys) {
				writer.write("\"" + key + "\" : ");
				writer.write("\"" + node.getValue(key) + "\"");

				if (index < keys.size()) {
					writer.write(",\n");
					writeIndents(writer, indentLevel);
				}

				index++;
			}

		} catch (IOException e) {
			throw new EncodeException("Failure to write QED value", e);
		}

	}

}
