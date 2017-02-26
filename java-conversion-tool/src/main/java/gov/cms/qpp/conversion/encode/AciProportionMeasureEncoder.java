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
	public void encode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node

		// the measure node will have 2 child nodes
		// one for the numerator and one for the denominator
		List<Node> children = node.getChildNodes();

		JsonWrapper childWrapper = new JsonWrapper<>();
		for (Node child : children) {
			JsonOutputEncoder denominatorValueEncoder = encoders.get(child.getId());
			denominatorValueEncoder.encode(childWrapper, child);
		}
		wrapper.put("measureId", node.getValue("measureId"));
		wrapper.put("value", childWrapper.getObject());
		
	}
}
