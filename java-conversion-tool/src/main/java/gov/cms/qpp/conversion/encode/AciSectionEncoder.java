package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

import java.util.List;

/**
 * Encoder to serialize ACI Section and it's measures
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.2.5")
public class AciSectionEncoder extends QppOutputEncoder {

	/**
	 *  Encodes an ACI Section into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the ACI Section
	 * @param node Node that represents the ACI Section
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {

		wrapper.putString("category", node.getValue("category"));
		List<Node> children = node.getChildNodes();
		JsonWrapper measurementsWrapper = new JsonWrapper();

		encodeChildren(children, measurementsWrapper);
		wrapper.putObject("measurements", measurementsWrapper);

	}

	private void encodeChildren(List<Node> children, JsonWrapper aciSectionsWrapper) {
		JsonWrapper childWrapper;
		for (Node currentChild : children) {
			childWrapper = new JsonWrapper();
			String templateId = currentChild.getId();
			JsonOutputEncoder childEncoder = encoders.get(templateId);

			if (childEncoder == null) {
				addValidation(templateId, "Failed to find an AciSectionEncoder");
			} else {
				childEncoder.encode(childWrapper, currentChild);
				aciSectionsWrapper.putObject(childWrapper);
			}
		}
	}
}
