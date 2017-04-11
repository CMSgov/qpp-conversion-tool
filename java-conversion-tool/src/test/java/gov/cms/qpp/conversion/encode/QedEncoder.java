package gov.cms.qpp.conversion.encode;
import gov.cms.qpp.conversion.model.EncoderNew;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Set;

/**
 * Encodes the "resultName" of the Q.E.D. node as a placeholder for testing
 */
@EncoderNew(TemplateId.QED)
public class QedEncoder extends QppOutputEncoder {

    /**
     * internal encode for QED placeholder nodes
     *
     * @param wrapper object to encode into
     * @param node object to encode
     * @throws EncodeException
     */
    @Override
    public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
        Set<String> keys = node.getKeys();

        for (String key : keys) {
            wrapper.putString(key, node.getValue(key));
        }
    }
}
