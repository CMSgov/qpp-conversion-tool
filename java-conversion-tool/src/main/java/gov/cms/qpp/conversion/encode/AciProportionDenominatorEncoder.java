package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

import java.util.List;

/**
 * Encoder to serialize Advancing Care Information Numerator Denominator Type
 * Measure Denominator Data.
 */

@Encoder(TemplateId.ACI_DENOMINATOR)
public class AciProportionDenominatorEncoder extends QppOutputEncoder {

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node

		// the ACI Proportion Denominator Node should have a single child
		// node that holds the value
		List<Node> children = node.getChildNodes();
		if (!children.isEmpty()) {
			Node denominatorValueNode = children.get(0);
			JsonOutputEncoder denominatorValueEncoder = encoders.get(denominatorValueNode.getId());

			JsonWrapper value = new JsonWrapper();
			denominatorValueEncoder.encode(value, denominatorValueNode);
			Integer denominator = value.getInteger("value");

			if (null != denominator) {
				wrapper.putObject("denominator", denominator);
			}
		}
	}
}
