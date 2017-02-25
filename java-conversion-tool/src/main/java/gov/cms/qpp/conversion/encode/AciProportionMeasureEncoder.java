package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.28")
public class AciProportionMeasureEncoder extends QppOutputEncoder {

	public AciProportionMeasureEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// simply writes the value in the Node

		try {
			writeIndents(writer, indentLevel);
			writer.write("\"measureId\" : \"");
			writer.write(node.getValue("measureId"));
			writer.write("\",\n");
			writeIndents(writer, indentLevel);
			writer.write("\"value\" : {\n");

			// the measure node will have 2 child nodes
			// one for the numerator and one for the denominator

			int index = 1;

			List<Node> children = node.getChildNodes();

			for (Node child : children) {
				JsonOutputEncoder denominatorValueEncoder = encoders.get(child.getId());

				denominatorValueEncoder.encode(writer, child, indentLevel + 1);

				if (index < children.size()) {
					writer.write(",\n");
				}
				index++;
			}

			writer.write("\n");
			writeIndents(writer, indentLevel);
			writer.write("}");

		} catch (IOException e) {
			throw new EncodeException("Failure to write an ACI Measure", e);
		}

	}

}
