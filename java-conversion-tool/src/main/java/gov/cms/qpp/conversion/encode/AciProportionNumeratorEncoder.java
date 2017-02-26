package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.31")
public class AciProportionNumeratorEncoder extends QppOutputEncoder {

	public AciProportionNumeratorEncoder() {
	}

	@Override
	public void encode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node

		// the ACI Proportion Numerator Node should have a single child node
		// that holds the value

		List<Node> children = node.getChildNodes();
		Node numeratorValueNode = children.get(0);
		JsonOutputEncoder numeratorValueEncoder = encoders.get(numeratorValueNode.getId());

		JsonWrapper value = new JsonWrapper();
		numeratorValueEncoder.encode(value, numeratorValueNode);
		
		wrapper.put("numerator", ((List<?>)value.getObject()).get(0));
	}
}
