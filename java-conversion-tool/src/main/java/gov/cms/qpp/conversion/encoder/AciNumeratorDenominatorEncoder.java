package gov.cms.qpp.conversion.encoder;

import java.io.IOException;
import java.io.Writer;

import gov.cms.qpp.conversion.model.JsonEncoder;
import gov.cms.qpp.conversion.model.Node;

@JsonEncoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AciNumeratorDenominatorEncoder extends QppOutputEncoder {

	public AciNumeratorDenominatorEncoder() {
	}

	@Override
	public void encode(Writer writer, Node node, int indentLevel) throws EncodeException {
		// simply writes the value in the Node

		try {
			writeIndents(writer, indentLevel);
			writer.write(node.getValue("aciNumeratorDenominator") + "\n");
		} catch (IOException e) {
			throw new EncodeException("Failure to write ACI Numerator/Denominator value", e);
		}

	}

}
