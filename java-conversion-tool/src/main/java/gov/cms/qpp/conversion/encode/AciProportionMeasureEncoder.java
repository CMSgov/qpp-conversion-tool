package gov.cms.qpp.conversion.encode;

import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.28")
public class AciProportionMeasureEncoder extends QppOutputEncoder {

	public AciProportionMeasureEncoder() {
	}

	@Override
	protected void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {
		// simply writes the value in the Node

		// the measure node will have 2 child nodes
		// one for the numerator and one for the denominator
		List<Node> children = node.getChildNodes();

		JsonWrapper childWrapper = new JsonWrapper();
		for (Node child : children) {
			String templateId = child.getId();
			JsonOutputEncoder denominatorValueEncoder = encoders.get(templateId);
			if (denominatorValueEncoder == null) {
				addValidation(templateId, "Failed to find an encoder");
			} else {
				denominatorValueEncoder.encode(childWrapper, child);
			}
		}
		wrapper.putObject("measureId", node.getValue("measureId"));
		wrapper.putObject("value", childWrapper.getObject());
		
	}
}
