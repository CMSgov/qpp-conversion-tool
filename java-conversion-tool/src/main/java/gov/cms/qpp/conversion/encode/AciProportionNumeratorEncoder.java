package gov.cms.qpp.conversion.encode;

import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.31")
public class AciProportionNumeratorEncoder extends QppOutputEncoder {

	public AciProportionNumeratorEncoder() {
	}

	@Override
	protected void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {
		// simply writes the value in the Node

		// the ACI Proportion Numerator Node should have a single child node
		// that holds the value

		List<Node> children = node.getChildNodes();
		Node numeratorValueNode = children.get(0);
		JsonOutputEncoder numeratorValueEncoder = encoders.get(numeratorValueNode.getId());

		JsonWrapper value = new JsonWrapper();
		numeratorValueEncoder.encode(value, numeratorValueNode);
		
		wrapper.putObject("numerator", ((List<?>)value.getObject()).get(0));
	}
}
