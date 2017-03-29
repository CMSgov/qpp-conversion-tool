package gov.cms.qpp.conversion.encode;

import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Encoder to serialize Improvement Activity Performed Measure Reference and Results.
 * @author David Puglielli
 *
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.33")
public class IaMeasureEncoder extends QppOutputEncoder {

	public IaMeasureEncoder() {
	}

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putObject("measureId", node.getValue("measureId"));
		
		List<Node> children = node.getChildNodes();

		if (!children.isEmpty()) {
			Node measurePerformedNode = children.get(0);
			JsonOutputEncoder measurePerformedEncoder = encoders.get(measurePerformedNode.getId());
	
			JsonWrapper value = new JsonWrapper();
			measurePerformedEncoder.encode(value, measurePerformedNode);
	
			if (null != value.getBoolean("value")) {
				wrapper.putObject("value", value.getBoolean("value"));
			}
		}
	}

}
