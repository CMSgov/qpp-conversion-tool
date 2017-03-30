package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import java.util.List;

/**
 * Encoder to serialize Advancing Care Information Numerator Denominator Type
 * Measure Numerator Data.
 *
 * @author Scott Fradkin
 *
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.31")
public class AciProportionNumeratorEncoder extends QppOutputEncoder {

    public AciProportionNumeratorEncoder() {
    }
    @Override
    protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		// simply writes the value in the Node

		// the ACI Proportion Numerator Node should have a single child node
        // that holds the value
        List<Node> children = node.getChildNodes();

        if (!children.isEmpty()) {
            Node numeratorValueNode = children.get(0);
            JsonOutputEncoder numeratorValueEncoder = encoders.get(numeratorValueNode.getId());

            JsonWrapper value = new JsonWrapper();
            numeratorValueEncoder.encode(value, numeratorValueNode);

            if (null != value.getInteger("value")) {
                wrapper.putObject("numerator", value.getInteger("value"));
            }
        }
    }
}
