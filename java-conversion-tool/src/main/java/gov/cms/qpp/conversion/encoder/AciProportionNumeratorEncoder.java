package gov.cms.qpp.conversion.encoder;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(elementName = "observation", templateId = "2.16.840.1.113883.10.20.27.3.31")
public class AciProportionNumeratorEncoder extends QppOutputEncoder {

	public AciProportionNumeratorEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// simply writes the value in the Node

		try {
			writeIndents(writer, indentLevel);
			writer.write("\"numerator\" : ");

			// the ACI Proportion Numerator Node should have a single child node
			// that holds the value

			List<Node> children = node.getChildNodes();
			Node numeratorValueNode = children.get(0);

			QppOutputEncoder numeratorValueEncoder = (QppOutputEncoder) encoders.get(numeratorValueNode.getIdElement(),
					numeratorValueNode.getIdTemplate());

			numeratorValueEncoder.encode(writer, numeratorValueNode, indentLevel + 1);

		} catch (IOException e) {
			throw new EncodeException("Failure to write ACI Numerator/Denominator value", e);
		}

	}

}
