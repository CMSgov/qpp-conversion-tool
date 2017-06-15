package gov.cms.qpp.conversion.encode;

import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to serialize Advancing Care Information Numerator Denominator Type
 * Measure Denominator Data.
 */

@Encoder(TemplateId.ACI_DENOMINATOR)
public class AciProportionDenominatorEncoder extends QppOutputEncoder {
	private static final String ENCODE_LABEL = "denominator";

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node

		// the ACI Proportion Denominator Node should have a single child
		// node that holds the value
		List<Node> children = node.getChildNodes();
		if (!children.isEmpty()) {
			Node denominatorValueNode = children.get(0);
			JsonOutputEncoder denominatorValueEncoder = ENCODERS.get(denominatorValueNode.getType());

			JsonWrapper value = new JsonWrapper();
			denominatorValueEncoder.encode(value, denominatorValueNode);

			if (null != value.getInteger(VALUE)) {
				wrapper.putObject(ENCODE_LABEL, value.getInteger(VALUE));
				wrapper.mergeMetadata(value, ENCODE_LABEL);
			}
		}
	}
}
