package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to serialize IA Section measures performed.
 */
@Encoder(TemplateId.MEASURE_PERFORMED)
public class MeasurePerformedEncoder extends QppOutputEncoder {

	/**
	 * internalEncode for measures performed
	 *
	 * @param wrapper object that will represent the measure performed
	 * @param node object that represents the measure performed
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.putBoolean(QppOutputEncoder.VALUE, node.getValue("measurePerformed"));
	}
}
