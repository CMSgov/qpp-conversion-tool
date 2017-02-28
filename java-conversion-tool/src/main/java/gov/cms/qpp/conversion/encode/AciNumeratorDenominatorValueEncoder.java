package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AciNumeratorDenominatorValueEncoder extends QppOutputEncoder {

	public AciNumeratorDenominatorValueEncoder() {
	}
	
	@Override
	public void encode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node
		wrapper.putObject(node.getValue("aciNumeratorDenominator"));
	}

}
