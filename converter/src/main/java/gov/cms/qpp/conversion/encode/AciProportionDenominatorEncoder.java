package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

import java.util.List;

/**
 * Encoder to serialize Advancing Care Information Numerator Denominator Type
 * Measure Denominator Data.
 */

@Encoder(TemplateId.ACI_DENOMINATOR)
public class AciProportionDenominatorEncoder extends QppOutputEncoder {

	private static final String ENCODE_LABEL = "denominator";

	public AciProportionDenominatorEncoder(Context context) {
		super(context);
	}

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node

		// the ACI Proportion Denominator Node should have a single child
		// node that holds the value
		Node denominatorValueNode = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);
		if (denominatorValueNode != null) {
			JsonOutputEncoder denominatorValueEncoder = encoders.get(denominatorValueNode.getType());

			JsonWrapper value = new JsonWrapper();
			denominatorValueEncoder.encode(value, denominatorValueNode);

			if (null != value.getInteger(VALUE)) {
				wrapper.putObject(ENCODE_LABEL, value.getInteger(VALUE));
				wrapper.mergeMetadata(value, ENCODE_LABEL);
			}
		}
	}
}
