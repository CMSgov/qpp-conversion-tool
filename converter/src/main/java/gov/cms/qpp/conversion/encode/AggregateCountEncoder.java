package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.validate.AggregateCountValidator;

/**
 * Encoder to serialize an Aggregate Count value type.
 *
 */
@Encoder(TemplateId.ACI_AGGREGATE_COUNT)
public class AggregateCountEncoder extends QppOutputEncoder {

	public AggregateCountEncoder(Context context) {
		super(context);
	}

	/**
	 * Copies the aggregate count to the output
	 *
	 * @param wrapper JsonWrapper
	 * @param node Node
	 * @throws EncodeException
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		// simply writes the value in the Node
		wrapper.putInteger(VALUE, node.getValue(AggregateCountDecoder.AGGREGATE_COUNT));
	}
}
