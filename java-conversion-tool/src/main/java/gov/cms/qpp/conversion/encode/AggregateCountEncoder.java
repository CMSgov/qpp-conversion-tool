package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Encoder to serialize an Aggregate Count value type.
 * @author Scott Fradkin
 *
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AggregateCountEncoder extends QppOutputEncoder {

	public AggregateCountEncoder() {
	}
	
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		// simply writes the value in the Node
		wrapper.putInteger("value", node.getValue("aggregateCount"));
	}

}
