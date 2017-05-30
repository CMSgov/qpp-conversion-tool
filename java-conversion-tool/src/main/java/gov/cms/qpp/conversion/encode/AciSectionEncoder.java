package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;

import java.util.List;

/**
 * Encoder to serialize ACI Section and it's measures
 */
@Encoder(TemplateId.ACI_SECTION)
public class AciSectionEncoder extends QppOutputEncoder {

	/**
	 *  Encodes an ACI Section into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the ACI Section
	 * @param node Node that represents the ACI Section
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
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
			TemplateId templateId = currentChild.getType();
			JsonOutputEncoder childEncoder = ENCODERS.get(templateId);

			if (childEncoder != null) {
				childEncoder.encode(childWrapper, currentChild);
				aciSectionsWrapper.putObject(childWrapper);
			} else {
				addValidationError(new ValidationError("Failed to find an AciSectionEncoder", currentChild.getPath()));
			}
		}
	}
}
