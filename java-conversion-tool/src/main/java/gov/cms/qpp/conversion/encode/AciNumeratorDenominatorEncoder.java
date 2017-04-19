package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize ACI Numerator Denominator Type Measure.
 */

@Encoder(TemplateId.ACI_NUMERATOR_DENOMINATOR)
public class AciNumeratorDenominatorEncoder extends QppOutputEncoder {

	/**
	 *  Encodes an ACI Numerator Denominator Type Measure into the QPP format
	 *
	 * @param wrapper Wrapper that will represent the ACI Numerator Denominator Type Measure
	 * @param node Node that represents the ACI Numerator Denominator Type Measure
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {

		//the measure node will have 2 child nodes
		//one for the numerator and one for the denominator

		//forcing toMap to use LinkedHashMap because we care about the order of the elements
		Map<String, Node> childMapByTemplateId = node.getChildNodes().stream().collect(
				Collectors.toMap(Node::getId, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

		//Performance Rate node not needed
		childMapByTemplateId.remove(TemplateId.PERFORMANCE_RATE.getTemplateId());

		JsonWrapper childWrapper = encodeChildren(childMapByTemplateId);

		wrapper.putObject("measureId", node.getValue("measureId"));
		wrapper.putObject("value", childWrapper);
	}

	/**
	 * Encodes ACI Numerator Denominator Children
	 *
	 * @param childMapByTemplateId Map of children that will be encoded
	 * @return JsonWrapper that will represent the encoded children
	 */
	private JsonWrapper encodeChildren(Map<String, Node> childMapByTemplateId) {
		JsonWrapper childWrapper = new JsonWrapper();
		for (Node currentChild : childMapByTemplateId.values()) {

			String templateId = currentChild.getId();
			JsonOutputEncoder childEncoder = ENCODERS.get(currentChild.getId());

			if (childEncoder == null) {
				addValidation(templateId, "Failed to find an encoder");
			} else {
				childEncoder.encode(childWrapper, currentChild);
			}
		}

		return childWrapper;
	}
}
