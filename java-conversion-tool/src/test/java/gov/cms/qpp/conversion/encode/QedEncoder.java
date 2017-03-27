package gov.cms.qpp.conversion.encode;

import java.util.Set;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Encodes the "resultName" of the Q.E.D. node as a placeholder for testing
 * 
 */
@Encoder(templateId = "Q.E.D")
public class QedEncoder extends QppOutputEncoder {

	public QedEncoder() {
	}

	@Override
	public void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {

		// the qed node should have one key/value in it
		Set<String> keys = node.getKeys();

		for (String key : keys) {
			wrapper.putString(key, node.getValue(key));
		}

	}

}
