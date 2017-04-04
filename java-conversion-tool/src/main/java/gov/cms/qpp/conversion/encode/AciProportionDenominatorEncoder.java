package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;

import java.util.List;

/**
 * Encoder to serialize Advancing Care Information Numerator Denominator Type
 * Measure Denominator Data.
 *
 * @author Scott Fradkin
 *
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.32")
public class AciProportionDenominatorEncoder extends QppOutputEncoder {

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		// simply writes the value in the Node

		// the ACI Proportion Denominator Node should have a single child
		// node that holds the value
		List<Node> children = node.getChildNodes();
		if (!children.isEmpty()) {
			Node denominatorValueNode = children.get(0);
			JsonOutputEncoder denominatorValueEncoder = ENCODERS.get(denominatorValueNode.getId());

			JsonWrapper value = new JsonWrapper();
			denominatorValueEncoder.encode(value, denominatorValueNode);
			Integer denominator = value.getInteger("value");

			if (null != denominator) {
				wrapper.putObject("denominator", denominator);
			}
		}
	}
}
