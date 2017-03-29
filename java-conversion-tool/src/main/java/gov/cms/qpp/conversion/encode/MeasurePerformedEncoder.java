package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Encoder to serialize Improvement Activity Section.
 * @author David Puglielli
 *
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.27")
public class MeasurePerformedEncoder extends QppOutputEncoder {

	public MeasurePerformedEncoder() {
	}

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putBoolean("value",node.getValue("measurePerformed"));
	}

}
