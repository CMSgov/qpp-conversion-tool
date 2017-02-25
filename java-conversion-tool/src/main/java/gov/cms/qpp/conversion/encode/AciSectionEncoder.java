package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.2.5")
public class AciSectionEncoder extends QppOutputEncoder {

	public AciSectionEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// simply writes the value in the Node

		try {
			writeIndents(writer, indentLevel);
			writer.write("\"category\" : \"");
			writer.write(node.getValue("category"));
			writer.write("\",\n");
			writeIndents(writer, indentLevel);
			writer.write("\"measurements\" : [\n");

			// there are some number of measurements as children

			int index = 1;

			List<Node> children = node.getChildNodes();

			for (Node child : children) {
				JsonOutputEncoder denominatorValueEncoder = encoders.get(child.getId());

				writeIndents(writer, indentLevel + 1);
				writer.write("{\n");

				denominatorValueEncoder.encode(writer, child, indentLevel + 2);

				writer.write("\n");
				writeIndents(writer, indentLevel + 1);
				writer.write("}");

				if (index < children.size()) {
					writer.write(",\n");
				}

				index++;
			}

			writer.write("\n");
			writeIndents(writer, indentLevel);
			writer.write("]");

		} catch (IOException e) {
			throw new EncodeException("Failure to write an ACI Section", e);
		}

	}

}
