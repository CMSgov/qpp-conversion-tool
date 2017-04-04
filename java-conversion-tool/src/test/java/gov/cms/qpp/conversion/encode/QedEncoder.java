package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import java.util.Set;

/**
 * Encodes the "resultName" of the Q.E.D. node as a placeholder for testing
 *
 */
@Encoder(templateId = "Q.E.D")
public class QedEncoder extends QppOutputEncoder {

    @Override
    public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
        // the qed node should have one key/value in it
        Set<String> keys = node.getKeys();

        for (String key : keys) {
            wrapper.putString(key, node.getValue(key));
        }
    }
}
