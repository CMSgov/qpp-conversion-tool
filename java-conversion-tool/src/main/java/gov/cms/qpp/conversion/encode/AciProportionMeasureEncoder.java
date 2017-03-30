package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize Advancing Care Information Numerator Denominator Type
 * Measure Reference and Results.
 *
 * @author Scott Fradkin
 *
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.28")
public class AciProportionMeasureEncoder extends QppOutputEncoder {

    public AciProportionMeasureEncoder() {
    }

    @Override
    protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		// simply writes the value in the Node

		// the measure node will have 2 child nodes
        // one for the numerator and one for the denominator
        Map<String, Node> childMapByTemplateId = node.getChildNodes().stream().collect(
                Collectors.toMap(Node::getId, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        // Performance Rate node not needed
        childMapByTemplateId.remove("2.16.840.1.113883.10.20.27.3.30");

        JsonWrapper childWrapper = new JsonWrapper();
        for (Node child : childMapByTemplateId.values()) {
            String templateId = child.getId();
            JsonOutputEncoder denominatorValueEncoder = encoders.get(child.getId());

            if (denominatorValueEncoder == null) {
                addValidation(templateId, "Failed to find an encoder");
            } else {
                denominatorValueEncoder.encode(childWrapper, child);
            }
        }
        wrapper.putObject("measureId", node.getValue("measureId"));
        wrapper.putObject("value", childWrapper);

    }
}
