package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to serialize Improvement Activity Section.
 *
 * @author David Puglielli
 *
 */
@Encoder(templateId = TemplateId.MEASURE_PERFORMED)
public class MeasurePerformedEncoder extends QppOutputEncoder {

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putBoolean("value", node.getValue("measurePerformed"));
	}
}
