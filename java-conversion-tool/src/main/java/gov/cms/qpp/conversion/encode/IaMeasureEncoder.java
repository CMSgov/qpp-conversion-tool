package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;

/**
 * Encoder to serialize Improvement Activity Performed Measure Reference and
 * Results.
 *
 * @author David Puglielli
 *
 */
@Encoder(templateId = TemplateId.IA_MEASURE)
public class IaMeasureEncoder extends QppOutputEncoder {

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putObject("measureId", node.getValue("measureId"));

		List<Node> children = node.getChildNodes();

		if (!children.isEmpty()) {
			Node measurePerformedNode = children.get(0);
			JsonOutputEncoder measurePerformedEncoder = ENCODERS.get(measurePerformedNode.getId());

			JsonWrapper value = new JsonWrapper();
			measurePerformedEncoder.encode(value, measurePerformedNode);

			if (null != value.getBoolean("value")) {
				wrapper.putObject("value", value.getBoolean("value"));
			}
		}
	}
}
