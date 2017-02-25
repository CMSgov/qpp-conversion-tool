package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.32")
public class AciProportionDenominatorEncoder extends QppOutputEncoder {

	public AciProportionDenominatorEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// simply writes the value in the Node

		try {
			writeIndents(writer, indentLevel);
			writer.write("\"denominator\" : ");

			// the ACI Proportion Denominator Node should have a single child
			// node
			// that holds the value

			List<Node> children = node.getChildNodes();
			Node denominatorValueNode = children.get(0);

			JsonOutputEncoder denominatorValueEncoder = encoders.get(denominatorValueNode.getId());

			denominatorValueEncoder.encode(writer, denominatorValueNode, indentLevel + 1);

		} catch (IOException e) {
			throw new EncodeException("Failure to write ACI Denominator", e);
		}

	}

}
