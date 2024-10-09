package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import static gov.cms.qpp.conversion.model.Constants.VALUE;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to serialize Promoting Interoperability Numerator Denominator Type
 * Measure Denominator Data.
 */

@Encoder(TemplateId.PI_DENOMINATOR)
public class PiProportionDenominatorEncoder extends QppOutputEncoder {

	private static final String ENCODE_LABEL = "denominator";

	public PiProportionDenominatorEncoder(Context context) {
		super(context);
	}

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node

		// the ACI Proportion Denominator Node should have a single child
		// node that holds the value
		Node denominatorValueNode = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);
		if (denominatorValueNode != null) {
			JsonOutputEncoder denominatorValueEncoder = encoders.get(denominatorValueNode.getType());

			JsonWrapper value = new JsonWrapper();
			denominatorValueEncoder.encode(value, denominatorValueNode);

			if (null != value.getInteger(VALUE)) {
				wrapper.put(ENCODE_LABEL, value.getInteger(VALUE));
				wrapper.mergeMetadata(value, ENCODE_LABEL);
			}
		}
	}
}
