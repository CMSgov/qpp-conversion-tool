package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import java.util.List;

/**
 * Encoder to serialize Advancing Care Information Section.
 *
 * @author Scott Fradkin
 *
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.2.5")
public class AciSectionEncoder extends QppOutputEncoder {

	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {

		wrapper.putString("category", node.getValue("category"));

		List<Node> children = node.getChildNodes();

		JsonWrapper measurementsWrapper = new JsonWrapper();

		JsonWrapper childWrapper;
		for (Node child : children) {
			childWrapper = new JsonWrapper();
			String templateId = child.getId();
			JsonOutputEncoder encoder = ENCODERS.get(templateId);
			if (encoder == null) {
				addValidation(templateId, "Failed to find an encoder");
			} else {
				encoder.encode(childWrapper, child);
				measurementsWrapper.putObject(childWrapper);
			}
		}
		wrapper.putObject("measurements", measurementsWrapper);
	}
}
