package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.List;

/**
 * Encoder to serialize numerator data from a Numerator/Denominator Type Measure.
 */
@Encoder(TemplateId.ACI_NUMERATOR)
public class AciProportionNumeratorEncoder extends QppOutputEncoder {

	/**
	 *  Encodes an ACI Numerator Measure into the QPP format
	 *
	 * @param wrapper Wrapper that will represent the ACI Numerator Measure
	 * @param node Node that represents the ACI Numerator Measure
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {

		List<Node> children = node.getChildNodes();

		if (!children.isEmpty()) {
			Integer numeratorValue = encodeChild(children.get(0));

			if (null != numeratorValue) {
				wrapper.putObject("numerator", numeratorValue);
			}
		}
	}

	private Integer encodeChild(Node numeratorValueNode) {
		JsonOutputEncoder numeratorValueEncoder = ENCODERS.get(numeratorValueNode.getId());

		JsonWrapper jsonWrapper = new JsonWrapper();
		numeratorValueEncoder.encode(jsonWrapper, numeratorValueNode);

		return jsonWrapper.getInteger(VALUE);
	}
}
