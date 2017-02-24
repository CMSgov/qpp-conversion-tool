package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AciNumeratorDenominatorValueEncoder extends QppOutputEncoder {

	public AciNumeratorDenominatorValueEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// simply writes the value in the Node

		try {
			// writeIndents(writer, indentLevel);
			// no indenting of the value... this is just a raw value that will
			// be included with a
			// numerator or denominator
			writer.write(node.getValue("aciNumeratorDenominator"));
		} catch (IOException e) {
			throw new EncodeException("Failure to write ACI Numerator/Denominator value", e);
		}

	}

}
