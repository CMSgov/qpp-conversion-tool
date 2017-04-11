package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.EncoderNew;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Set;

/**
 * Encodes the "resultName" of the Q.E.D. node as a placeholder for testing
 *
 */
@EncoderNew(TemplateId.QED)
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
